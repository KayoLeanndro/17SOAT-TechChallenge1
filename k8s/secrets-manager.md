# Credenciais no AWS Secrets Manager

Este e o fluxo principal de credenciais para o EKS. O arquivo `secret.local.yaml` fica como alternativa de deploy manual, ignorada pelo Git, e nao deve ser aplicado em paralelo ao External Secrets Operator (ESO).

## Como funciona

1. O Terraform cria o segredo `mechanics-api/dev/application`, inicialmente sem valores, o complemento EKS Pod Identity Agent, uma role IAM e sua associacao ao ServiceAccount do operador.
2. Voce cadastra o JSON com os valores no Secrets Manager.
3. O Helm instala o ESO no namespace `external-secrets`, usando o ServiceAccount `external-secrets`.
4. O `SecretStore` informa o servico AWS e a regiao. A autenticacao usa credenciais temporarias do Pod Identity do operador, sem chaves AWS estaticas nos manifestos.
5. O `ExternalSecret` mapeia dois campos do JSON para o Secret Kubernetes `mechanics-api-secrets`, no namespace `default`.
6. O Deployment recebe as variaveis via `secretRef`, como ja estava configurado.

O operador foi configurado para reconciliar o namespace `default`. Sua permissao AWS permite apenas `GetSecretValue` e `DescribeSecret` no ARN exato do segredo criado. Ele nao altera a senha do RDS nem os valores armazenados na AWS.

## 1. Validar e provisionar a estrutura

Todos os comandos deste guia partem da raiz do repositorio. Requer Terraform inicializado, AWS CLI autenticada e acesso a conta e ao cluster de destino.

```powershell
terraform -chdir=infra/terraform/environments/dev fmt -check
terraform -chdir=infra/terraform/environments/dev validate
terraform -chdir=infra/terraform/environments/dev plan -out=tfplan
```

Se o Terraform solicitar `db_password`, informe a mesma senha atual do RDS. O parametro continua necessario porque o banco ainda usa `password = var.db_password`. A pipeline agora le a senha do Secrets Manager e a exporta como `TF_VAR_db_password`; o GitHub Secret `DB_PASSWORD` nao e mais utilizado. Consulte o [fluxo de migrations e deploy](migrations.md).

Revise o plano completo antes de executar o comando abaixo. As adicoes esperadas desta implementacao sao o segredo, a role, sua politica inline, o complemento e a associacao Pod Identity. O plano tambem pode apontar outras diferencas existentes; confira cada uma.

```powershell
terraform -chdir=infra/terraform/environments/dev apply tfplan
terraform -chdir=infra/terraform/environments/dev output -raw application_secret_name
```

O Terraform gerencia apenas os metadados deste novo segredo; nao ha recurso `aws_secretsmanager_secret_version` nem leitura dos valores no codigo Terraform. A senha do RDS continua sujeita ao estado do recurso de banco existente. O bucket de estado deve manter seu controle de acesso.

Se ja existir um segredo com esse nome, importe-o para o endereco `aws_secretsmanager_secret.application` usando o ARN antes de planejar sua criacao. Ao destruir este recurso, o Secrets Manager agenda a exclusao com janela de recuperacao de sete dias.

## 2. Cadastrar os valores na AWS

No console AWS, selecione a regiao `us-east-1` e abra **Secrets Manager**. Localize o segredo com o nome retornado pelo output. Cadastre uma versao com os seguintes campos, pelo editor de pares chave/valor ou de texto JSON:

```json
{
  "SPRING_DATASOURCE_PASSWORD": "SENHA_ATUAL_DO_RDS",
  "JWT_SECRET": "CHAVE_JWT_DO_AMBIENTE"
}
```

Os textos acima sao marcadores, nao valores para salvar. Use a senha atual do RDS e a chave aleatoria que voce ja preparou no arquivo local, sem envia-las para o Git ou para o chat. Editar esse JSON nao troca a senha no PostgreSQL: ambos precisam continuar alinhados.

Use a chave de criptografia padrao do Secrets Manager nesta configuracao. Se escolher uma chave KMS propria, adicione tambem a autorizacao de decriptacao apropriada na role e na politica da chave.

## 3. Instalar o operador

Requer Helm instalado no computador (ou em um ambiente administrativo com acesso ao EKS). Consulte a [instalacao oficial do Helm](https://helm.sh/docs/intro/install/). O Helm nao foi encontrado no ambiente local durante a preparacao desta integracao.

Configure o kubectl para o cluster. Com os valores padrao do projeto:

```powershell
aws eks update-kubeconfig --region us-east-1 --name mechanics-api-dev
kubectl config current-context
helm repo add external-secrets https://charts.external-secrets.io
helm repo update
helm upgrade --install external-secrets external-secrets/external-secrets --version 2.13.0 --namespace external-secrets --create-namespace --values infra/helm/external-secrets/values.yaml --wait --timeout 5m
kubectl rollout status deployment/external-secrets --namespace external-secrets --timeout=300s
```

O chart instala as CRDs, que ensinam o Kubernetes a reconhecer `SecretStore` e `ExternalSecret`. O nome do ServiceAccount e seu namespace devem coincidir com a associacao criada pelo Terraform. Se o operador ja estava instalado antes da associacao Pod Identity, recrie seus pods com `kubectl rollout restart deployment/external-secrets --namespace external-secrets` e aguarde o rollout novamente.

A identidade que aplica Terraform precisa das permissoes para os novos recursos, incluindo `iam:PassRole` para a associacao. A role dos nos precisa permitir o uso do Pod Identity Agent; o projeto ja associa `AmazonEKSWorkerNodePolicy`. Os pods precisam de conectividade com os endpoints AWS e o cluster.

## 4. Sincronizar o Secret

Confira `region` em `secret-store.yaml` e `remoteRef.key` em `external-secret.yaml` se voce sobrescreveu regiao ou nome do segredo no Terraform. Aplique os arquivos explicitamente:

```powershell
kubectl apply -f k8s/secret-store.yaml
kubectl wait --for=condition=Ready secretstore/mechanics-api-aws --namespace default --timeout=120s
kubectl apply -f k8s/external-secret.yaml
kubectl wait --for=condition=Ready externalsecret/mechanics-api --namespace default --timeout=120s
kubectl get secret mechanics-api-secrets --namespace default
```

O ultimo comando mostra metadados, sem revelar os valores. Se ja houver um Secret manual sem outro controlador proprietario, o ESO pode assumir sua gestao com `creationPolicy: Owner`. Nao continue aplicando `secret.local.yaml` apos a migracao. Caso haja conflito de proprietario, inspecione os metadados antes de alterar recursos; nao exclua o Secret em uso para resolver o conflito automaticamente.

O Deployment continua usando `mechanics-api-secrets`, sem precisar conhecer o Secrets Manager. Aplique o Deployment somente quando a sincronizacao tiver sucesso e a imagem e o ConfigMap estiverem preenchidos.

## 5. Atualizacoes e diagnostico

O operador consulta os valores a cada hora. Para antecipar uma sincronizacao apos editar os valores na AWS:

```powershell
kubectl annotate externalsecret mechanics-api --namespace default force-sync=$(Get-Date -UFormat %s) --overwrite
kubectl wait --for=condition=Ready externalsecret/mechanics-api --namespace default --timeout=120s
```

Um `Ready=True` anterior pode continuar presente enquanto a nova reconciliacao nao terminou. Confira o `status.refreshTime` e os eventos para confirmar que a nova versao foi sincronizada antes de reiniciar os pods.

Como a API recebe variaveis de ambiente, processos ja iniciados nao recarregam os novos valores. Depois de confirmar a sincronizacao, se o Deployment ja estiver em execucao:

```powershell
kubectl rollout restart deployment/mechanics-api --namespace default
kubectl rollout status deployment/mechanics-api --namespace default --timeout=600s
```

Trocar a chave JWT invalida os tokens assinados com a chave anterior. Durante um rollout com chaves diferentes pode haver falhas de autenticacao entre replicas. Planeje essa troca; a aplicacao atual nao possui um mecanismo de transicao entre chaves.

Para investigar falhas sem imprimir as credenciais:

```powershell
kubectl describe secretstore mechanics-api-aws --namespace default
kubectl describe externalsecret mechanics-api --namespace default
kubectl logs deployment/external-secrets --namespace external-secrets --tail=100
```

`AccessDenied` indica verificar IAM/Pod Identity; segredo ou propriedade ausentes indicam conferir nome, regiao, versao cadastrada e chaves do JSON. Nao publique o conteudo do Secret ao compartilhar diagnosticos.

## Pipeline e limites desta etapa

O workflow aplica o Terraform, publica a imagem e executa `scripts/deploy-eks.sh` em pushes da `main`. O script instala o operador por Helm, aplica SecretStore e ExternalSecret e aguarda a sincronizacao antes de iniciar a API. O cadastro inicial dos valores no Secrets Manager continua sendo um pre-requisito manual; o script nao cria nem altera senhas. Consulte o [guia de deploy e escala](deploy-eks.md).

O arquivo `secret.local.yaml` nao foi lido, alterado nem enviado a AWS durante a preparacao desta integracao. A sincronizacao no EKS so pode ser confirmada depois do provisionamento, cadastro dos valores e instalacao do operador.

## Referencias

- [Autenticacao AWS e EKS Pod Identity no ESO](https://external-secrets.io/latest/provider/aws-access/)
- [AWS Secrets Manager no ESO](https://external-secrets.io/latest/provider/aws-secrets-manager/)
- [Instalacao do operador com Helm](https://external-secrets.io/latest/introduction/getting-started/)
- [Trust policy do EKS Pod Identity](https://docs.aws.amazon.com/eks/latest/userguide/pod-id-role.html)
