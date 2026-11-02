# Kubernetes — configurações e Deployment

Esta pasta contém as configurações, o Deployment, o Service e o HPA da API. O fluxo automatizado está no [guia de deploy e escala](deploy-eks.md). Nenhum recurso é aplicado no cluster apenas por criar estes arquivos.

O acesso público HTTP à API e ao Swagger usa `ingress.yaml` e o AWS Load Balancer Controller. Consulte o [guia do ALB](alb.md).

**Credenciais no EKS:** siga primeiro o [guia do AWS Secrets Manager](secrets-manager.md). O fluxo principal agora usa External Secrets Operator com EKS Pod Identity para gerar `mechanics-api-secrets`. Os passos de Secret local abaixo são uma alternativa manual, sem aplicação paralela ao operador.

## Entenda os arquivos

`configmap.yaml` declara um ConfigMap chamado `mechanics-api-config`. Ele agrupa configurações não sensíveis que o Deployment fornece ao container como variáveis de ambiente.

`secret.yaml.example` declara o modelo de um Secret chamado `mechanics-api-secrets`. Ele separa a senha do PostgreSQL e a chave JWT das configurações comuns. `Opaque` significa um Secret de uso geral. `stringData` permite preencher os valores como texto; não é necessário convertê-los manualmente em base64. Um Secret não torna seguro publicar credenciais no Git, e base64 não é criptografia.

Nos dois manifestos, `apiVersion` e `kind` identificam o tipo de recurso; `metadata` define nome e labels. `data` contém os valores do ConfigMap; `stringData` contém os valores de entrada do Secret. Os valores estão entre aspas porque são strings, inclusive a porta.

| Variável | Origem | Uso na aplicação |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | ConfigMap | Substitui `spring.datasource.url`. |
| `SPRING_DATASOURCE_USERNAME` | ConfigMap | Substitui `spring.datasource.username`. |
| `SERVER_PORT` | ConfigMap | Substitui `server.port`; mantemos 8080. |
| `SPRING_DATASOURCE_PASSWORD` | Secret | Substitui `spring.datasource.password`. |
| `JWT_SECRET` | Secret | Preenche a propriedade `jwt.secret` já existente. |

O Spring Boot permite que variáveis de ambiente substituam propriedades do arquivo empacotado. Assim, a mesma imagem Docker pode receber configurações diferentes no Compose e no Kubernetes.

## 1. Preencher os dados do banco

Na raiz do repositório, com o backend Terraform já inicializado e o RDS provisionado, consulte:

```powershell
terraform -chdir=infra/terraform/environments/dev output -raw db_address
terraform -chdir=infra/terraform/environments/dev output -raw db_name
terraform -chdir=infra/terraform/environments/dev output db_port
```

Edite `k8s/configmap.yaml` e substitua o marcador `SUBSTITUA_PELO_ENDPOINT_RDS` pelo endereço retornado, sem adicionar protocolo HTTP. A URL JDBC fica no formato `jdbc:postgresql://ENDERECO:PORTA/BANCO`. Confira também o usuário: o padrão do Terraform é `mechanics_admin`, mas ele pode ter sido sobrescrito no provisionamento.

Se o RDS ainda não existir, mantenha o marcador durante a preparação dos arquivos. Ele precisa ser substituído antes de executar a aplicação.

## 2. Alternativa manual: preparar o Secret local

Na raiz do repositório:

```powershell
Copy-Item k8s/secret.yaml.example k8s/secret.local.yaml
```

Edite `secret.local.yaml` no seu editor:

- Em `SPRING_DATASOURCE_PASSWORD`, informe a mesma senha usada para provisionar o RDS.
- Em `JWT_SECRET`, informe uma chave aleatória própria do ambiente, com pelo menos 32 bytes. Todas as réplicas da API devem usar a mesma chave para validar os tokens.
- Preserve a sintaxe YAML; aspas simples no valor precisam ser duplicadas se você optar por strings delimitadas por aspas simples.

O arquivo `secret.local.yaml` está ignorado no Git. Confirme a regra antes de preencher as credenciais:

```powershell
git check-ignore k8s/secret.local.yaml
```

Esse arquivo é para uso local no procedimento manual. No fluxo principal, o operador sincroniza o Secret a partir do Secrets Manager. A pipeline lê a senha atual desse segredo para o Terraform; o GitHub Secret `DB_PASSWORD` não é mais utilizado. Não use os marcadores do exemplo como credenciais.

## 3. Alternativa manual: validar e aplicar o Secret local

Confira o contexto do `kubectl`. Todos os comandos abaixo usam o namespace `default`; o futuro Deployment deve usar o mesmo namespace para acessar estes recursos.

```powershell
kubectl config current-context
kubectl apply --dry-run=server --namespace default -f k8s/configmap.yaml
kubectl apply --dry-run=server --namespace default -f k8s/secret.local.yaml
```

O dry-run de servidor requer conexão e permissão no cluster, mas não persiste os recursos. Depois de preencher os valores, conferir o contexto e validar os manifestos, aplique:

```powershell
kubectl apply --namespace default -f k8s/configmap.yaml
kubectl apply --namespace default -f k8s/secret.local.yaml
kubectl get configmap mechanics-api-config --namespace default
kubectl get secret mechanics-api-secrets --namespace default
```

Esses comandos de consulta mostram metadados sem imprimir os valores das credenciais. Use os arquivos explicitamente: o Secret real fica em um arquivo local separado do exemplo.

## 4. Como o Deployment usa isso

O container de `deployment.yaml` referencia as configurações desta forma:

```yaml
envFrom:
  - configMapRef:
      name: mechanics-api-config
  - secretRef:
      name: mechanics-api-secrets
```

Criar um ConfigMap ou Secret não injeta variáveis automaticamente. É essa referência no Deployment que conecta os recursos ao container. Alterações nessas variáveis exigem substituir os pods para que os novos processos recebam os valores atualizados.

**Conclusão desta etapa:** os arquivos de configuração e o procedimento estão preparados. A conexão real ao RDS será validada após criar e executar o Deployment.

## 5. Entenda o Deployment

O arquivo `deployment.yaml` declara como manter a API em execução:

| Campo | Configuração inicial | Significado |
| --- | --- | --- |
| Réplicas | De 2 a 6 pelo HPA | O Deployment omite `spec.replicas` para preservar a escala automática. |
| `selector` e labels do template | `app: mechanics-api` | Identificam os pods gerenciados pelo Deployment. |
| `image` | Marcador a substituir | Referência completa da imagem no ECR, com tag do commit. |
| `envFrom` | ConfigMap e Secret | Fornece variáveis de ambiente ao processo Java. |
| `requests` | CPU `250m`, memória `512Mi` | Recursos considerados pelo agendador ao escolher um nó. `250m` corresponde a 0,25 CPU. |
| `limits` | CPU `1`, memória `1Gi` | Limites de consumo do container; ultrapassar a memória pode causar encerramento por falta de memória. |
| `RollingUpdate` | Um pod extra e zero indisponíveis durante a atualização | Cria uma nova réplica antes de remover uma antiga; requer capacidade para o pod extra. |

Esses valores são um ponto de partida, não uma medição do consumo. Precisam ser ajustados após observar a aplicação e testar carga. As réplicas não têm garantia de distribuição em nós diferentes nesta configuração.

### Verificações de saúde

Foi adicionada a dependência `spring-boot-starter-actuator`. A aplicação expõe as probes na porta 8080, sem token e sem detalhes internos:

- `startupProbe`: consulta `/actuator/health/liveness` e concede aproximadamente cinco minutos para a inicialização antes de reiniciar o container. Enquanto ela não tiver sucesso, as outras probes ficam suspensas.
- `readinessProbe`: consulta `/actuator/health/readiness`, incluindo a saúde do banco. Uma falha impede o pod de receber tráfego pelo Service quando ele for criado.
- `livenessProbe`: consulta `/actuator/health/liveness`, sem incluir o banco. Falhas consecutivas fazem o Kubernetes reiniciar o container; uma indisponibilidade do RDS não deve, por si só, disparar esses reinícios.

A configuração de segurança libera somente os dois caminhos de probes; o health geral e as APIs de negócio continuam exigindo autenticação. O prazo de inicialização pode precisar de ajuste para migrations longas.

## 6. Preparar a imagem e aplicar o Deployment

Primeiro gere e publique uma nova imagem que contenha a dependência Actuator e as alterações das probes. Imagens antigas não oferecem esses endpoints e falharão nas verificações. O workflow publica imagens e executa o deploy em pushes da `main`, conforme o [guia de deploy e escala](deploy-eks.md).

Consulte a URL do repositório:

```powershell
terraform -chdir=infra/terraform/environments/dev output -raw ecr_repository_url
```

No campo `image` de `deployment.yaml`, use a URL retornada seguida de `:` e da tag do commit efetivamente publicada. Os marcadores do arquivo precisam ser substituídos. ConfigMap, Secret e Deployment devem estar no mesmo namespace.

Depois de preencher a imagem e os dados de conexão, criar o Secret e conferir o contexto do cluster, execute na raiz do projeto:

```powershell
kubectl apply --dry-run=server --namespace default -f k8s/deployment.yaml
kubectl apply --namespace default -f k8s/deployment.yaml
kubectl rollout status deployment/mechanics-api --namespace default --timeout=600s
kubectl get pods --namespace default -l app=mechanics-api
kubectl logs deployment/mechanics-api --namespace default --tail=100
```

Em caso de falha, consulte `kubectl describe pod NOME_DO_POD --namespace default`. O dry-run verifica o recurso, mas não garante que a imagem existe, que as credenciais estão corretas ou que o banco está acessível.

Para testar uma réplica antes de criar o Service:

```powershell
kubectl port-forward --namespace default deployment/mechanics-api 8080:8080
```

Em outro terminal:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health/liveness
Invoke-RestMethod http://localhost:8080/actuator/health/readiness
```

Use uma porta local livre. A verificação pelo cluster e o acesso real ao RDS permanecem pendentes até executar este procedimento. O Service já está em `service.yaml`: ele seleciona os pods com `app: mechanics-api` e encaminha a porta 8080 para a porta nomeada `http` do container. Depois de aplicar o Deployment e aguardar o rollout, aplique o Service com `kubectl apply --namespace default -f k8s/service.yaml`.

## Referências

- [ConfigMaps no Kubernetes](https://kubernetes.io/docs/concepts/configuration/configmap/)
- [Secrets no Kubernetes](https://kubernetes.io/docs/concepts/configuration/secret/)
- [Configuração externa no Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Endpoints do Actuator](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html)
- [Probes do Kubernetes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)
