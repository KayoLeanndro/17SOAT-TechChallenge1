# Acesso público à API e ao Swagger

O Ingress `mechanics-api` cria um Application Load Balancer público para HTTP na porta 80. Ele encaminha as requisições aos IPs dos pods na porta 8080, usando o Service `mechanics-api` para descobrir os destinos. O Service permanece `ClusterIP`; o banco continua privado.

Validado em 29/10/2026:

- [Swagger público](http://k8s-default-mechanic-9378e9930d-2028155602.us-east-1.elb.amazonaws.com/swagger-ui/index.html).
- [OpenAPI JSON](http://k8s-default-mechanic-9378e9930d-2028155602.us-east-1.elb.amazonaws.com/v3/api-docs).
- Base para requisições: `http://k8s-default-mechanic-9378e9930d-2028155602.us-east-1.elb.amazonaws.com/api`.

O ALB estava ativo com três destinos saudáveis. Swagger e OpenAPI responderam HTTP 200; `/api/cliente` respondeu 401 sem JWT. O DNS pode mudar se o ALB for recriado.

Os caminhos não são reescritos. Use o DNS do ALB como base:

| Recurso | Caminho |
| --- | --- |
| Swagger UI | `/swagger-ui/index.html` |
| OpenAPI JSON | `/v3/api-docs` |
| Login | `POST /api/auth/login` |
| Exemplo de rota protegida | `/api/cliente` |

Swagger e login mantêm as permissões públicas configuradas na aplicação. As rotas protegidas continuam exigindo `Authorization: Bearer TOKEN`. O ALB verifica `/actuator/health/readiness`, esperando HTTP 200, e acompanha a entrada e saída de pods do HPA.

## Arquivos e identidade

- `infra/terraform/environments/dev/load-balancer.tf`: role e política IAM, associação EKS Pod Identity e tags de descoberta das sub-redes públicas da VPC default.
- `infra/iam/aws-load-balancer-controller-v3.6.2.json`: política oficial correspondente ao controlador.
- `infra/helm/aws-load-balancer-controller/values.yaml`: duas réplicas, ServiceAccount `aws-load-balancer-controller` em `kube-system` e Gateway API desabilitada, pois usamos Ingress.
- `k8s/ingress.yaml`: regras HTTP, classe `alb`, esquema público e destinos do tipo `ip`.
- `scripts/expose-eks.sh`: instalação do chart 3.6.2, aplicação do Ingress e teste público do Swagger/OpenAPI. Chamado automaticamente pelo script de deploy após o rollout.

O controlador usa credenciais temporárias do Pod Identity, sem chaves AWS nos YAMLs. O Terraform gerencia a identidade e as tags; o controlador Kubernetes gerencia o ALB, target groups e security groups associados ao Ingress.

## Aplicação manual

Requer Terraform aplicado com os novos recursos IAM, AWS autenticada, kubeconfig do cluster correto, Helm, kubectl, Bash, curl e jq. Na raiz do projeto:

```powershell
aws eks update-kubeconfig --profile kayo-leanndro --region us-east-1 --name mechanics-api-dev
```

Em Bash, com `AWS_PROFILE` configurado quando necessário:

```bash
bash scripts/expose-eks.sh
```

O script aguarda o ALB ficar ativo e imprime os endereços quando o Swagger e o OpenAPI respondem pelo balanceador. Para consultá-los novamente:

```powershell
kubectl get ingress mechanics-api -n default
kubectl describe ingress mechanics-api -n default
```

Se a criação falhar, confira os eventos do Ingress e os logs de `deployment/aws-load-balancer-controller` em `kube-system`. A identidade do controlador deve coincidir com a associação Terraform, e as sub-redes marcadas precisam de rota para o Internet Gateway.

Esta configuração utiliza HTTP. HTTPS requer domínio e certificado ACM apropriado, além do listener 443 e redirecionamento no Ingress.

Para remover o ambiente, exclua primeiro o Ingress e aguarde a remoção do ALB e seus recursos pelo controlador, mantendo o EKS e o controlador ativos até concluir essa etapa. Depois execute o destroy do Terraform. O ALB não faz parte diretamente do state Terraform.

Referências: [instalação e Pod Identity](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/deploy/installation/), [anotações do Ingress](https://kubernetes-sigs.github.io/aws-load-balancer-controller/latest/guide/ingress/annotations/), [política IAM v3.6.2](https://github.com/kubernetes-sigs/aws-load-balancer-controller/blob/v3.6.2/docs/install/iam_policy.json).
