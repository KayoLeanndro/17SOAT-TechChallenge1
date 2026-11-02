# Deploy no EKS e escala dos pods

O workflow `.github/workflows/tech-challenge.yml` executa testes, provisiona a infraestrutura, publica a imagem no ECR e executa `scripts/deploy-eks.sh` em pushes da `main`. Pull requests executam somente testes e build. A implementação precisa ser enviada ao GitHub para que esse fluxo seja executado.

## Preparação da conta

Configure no repositório GitHub:

| Tipo | Nome | Conteúdo |
| --- | --- | --- |
| Variable | `AWS_ROLE_ARN` | Role assumida via OIDC pelo GitHub Actions. |
| Variable | `AWS_REGION` | Região do ambiente, por padrão `us-east-1`. |
| Variable | `TF_STATE_BUCKET` | Bucket do backend S3 já criado pelo bootstrap. |

A role precisa de trust OIDC para o repositório e a branch `main`, permissões para os recursos Terraform, leitura do backend e publicação/consulta de imagens ECR. Também precisa de `eks:DescribeCluster` e autorização Kubernetes para instalar o operador/CRDs e aplicar os recursos. Inclua sua ARN em `eks_admin_principal_arns` se ela diferir do padrão. A autorização administrativa existente permite a instalação; para ambientes adicionais, planeje permissões mais restritas.

O Terraform usa sua região padrão; se mudar `AWS_REGION`, alinhe também `TF_VAR_aws_region` nos jobs e a configuração de região do backend S3 ao bucket utilizado. A rede atual depende da VPC default e filtra zonas de `us-east-1`.

Antes da pipeline, cadastre no Secrets Manager `mechanics-api/dev/application` os campos `SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET`, conforme [o guia de segredos](secrets-manager.md). A senha deve ser a mesma do RDS. A pipeline lê essa senha para o Terraform; o GitHub Secret `DB_PASSWORD` não é mais utilizado. Em uma conta nova, prepare primeiro o bootstrap, o state do ambiente e o segredo conforme o [guia de migrations](migrations.md). O deploy espera até três minutos pela sincronização e falha antes de alterar o Deployment se ela não ocorrer.

## O que o deploy faz

1. Lê os outputs públicos do Terraform e confere a conta AWS da imagem.
2. Cria um kubeconfig temporário para o cluster retornado pelo Terraform.
3. Instala o External Secrets Operator no namespace `external-secrets`.
4. Configura região e nome do segredo a partir dos outputs e espera o ExternalSecret ficar pronto.
5. Gera o ConfigMap usando endereço, porta, nome e usuário reais do RDS.
6. Executa um Job de migrations com a mesma imagem e aguarda sucesso; uma falha bloqueia o rollout.
7. Aplica a imagem do commit ao Deployment, além de Service e HPA no namespace `default`.
8. Aguarda o rollout, a API de métricas e a condição `ScalingActive` do HPA.
9. Instala o AWS Load Balancer Controller, aplica o Ingress público e valida Swagger e OpenAPI pelo ALB, conforme o [guia de acesso público](alb.md).

O ConfigMap versionado é um modelo para aplicação manual; o script gera sua versão a partir dos outputs, sem editar o arquivo. Mudanças de configuração alteram uma anotação do template e provocam a substituição dos pods. Atualizações de Secret por variáveis de ambiente exigem reiniciar os pods depois de confirmar a sincronização, conforme o guia de segredos.

O ECR mantém tags imutáveis. Reexecutar a pipeline no mesmo commit reutiliza a imagem existente. O provisionamento dispõe de 60 minutos e as execuções da mesma branch são serializadas para evitar deploys simultâneos.

## HPA

`hpa.yaml` usa `autoscaling/v2` e controla o Deployment `mechanics-api`:

| Parâmetro | Valor inicial |
| --- | --- |
| Mínimo / máximo | 2 / 6 pods |
| CPU média desejada | 70% do request de `250m`, equivalente a `175m` por pod. |
| Memória média desejada | 80% do request de `512Mi`, aproximadamente `410Mi` por pod. |
| Aumento | Até 2 pods por minuto. |
| Redução | Janela de estabilização de 5 minutos; até 1 pod por minuto. |

O HPA considera a maior recomendação das métricas. Os valores são iniciais e devem ser ajustados medindo a API: o consumo de memória da JVM pode manter a escala elevada mesmo após a carga diminuir. Os requests precisam representar o consumo real. O Deployment não define `replicas`, para que novos deploys não sobrescrevam a escala. Ao migrar um Deployment antigo gerenciado por `kubectl apply`, a primeira remoção desse campo pode causar uma redução transitória antes da reconciliação pelo HPA.

O HPA aumenta pods, não cria máquinas EC2. Se os nós ficarem sem capacidade, haverá pods `Pending`. O node group atual tem tamanho desejado de dois nós; seus limites não instalam um autoscaler de nós. Considere também os limites de conexões e capacidade do RDS antes de aumentar `maxReplicas`.

## Execução manual

Requer Bash (Linux ou Git Bash), AWS CLI autenticada na conta correta, Terraform, kubectl compatível com o cluster, Helm 3, jq e sha256sum. O backend deve estar inicializado e o Terraform aplicado com os novos outputs. A imagem já deve existir no ECR.

Na raiz do projeto, em Bash:

```bash
export AWS_PROFILE=SEU_PERFIL
terraform -chdir=infra/terraform/environments/dev init -reconfigure -backend-config="bucket=SEU_BUCKET"
# Depois de revisar e aplicar o plano Terraform e publicar a imagem:
bash scripts/deploy-eks.sh URL_DO_ECR:TAG_DO_COMMIT
```

Não aplique toda a pasta `k8s` indiscriminadamente: existem modelos e um Secret local que não fazem parte do fluxo do operador.

## Conferência após o deploy

Configure seu próprio kubeconfig, pois o script usa um arquivo temporário:

```powershell
aws eks update-kubeconfig --region us-east-1 --name mechanics-api-dev
kubectl get pods -n default -l app=mechanics-api
kubectl get hpa mechanics-api -n default -w
kubectl top pods -n default -l app=mechanics-api
kubectl describe hpa mechanics-api -n default
```

Em caso de métricas `<unknown>`, confira `kubectl get apiservice v1beta1.metrics.k8s.io` e os eventos do HPA. Aguarde a coleta inicial após a inicialização dos pods. Para investigar falta de capacidade, use `kubectl describe pod NOME_DO_POD -n default`.

O Service continua interno (`ClusterIP`), com acesso público pelo [Ingress ALB](alb.md). Para conferir a API localmente por uma alternativa administrativa:

```powershell
kubectl port-forward -n default service/mechanics-api 8080:8080
```

Em outro terminal, consulte `http://localhost:8080/actuator/health/readiness` e faça login normalmente. Para observar a escala, execute carga HTTP contra uma rota representativa durante alguns minutos e acompanhe HPA e `kubectl top`; uma rota simples de saúde pode não gerar CPU suficiente. Após encerrar a carga, a redução respeita a janela de estabilização e a memória observada. Este procedimento não substitui um teste de carga real no EKS.

Referências: [HPA e comportamento da escala](https://kubernetes.io/docs/concepts/workloads/autoscaling/horizontal-pod-autoscale/), [escala de nós](https://kubernetes.io/docs/concepts/cluster-administration/node-autoscaling/).
