# Deploy AWS de 29/10/2026

O ambiente dev foi recriado na conta AWS `570903461728`, em `us-east-1`, com o perfil `kayo-leanndro`, após autorização para provisionar EKS, EC2 e RDS.

## Recursos e imagem

- Backend: bucket `mechanics-api-tfstate-570903461728`, chave `environments/dev/terraform.tfstate`.
- Terraform: 21 recursos criados, um atualizado e nenhuma exclusão.
- EKS: `mechanics-api-dev`, Kubernetes 1.37; dois nós `Ready`.
- RDS: `mechanics-api-dev-postgres`, PostgreSQL 16, banco `mechanics`, acesso privado.
- ECR: `570903461728.dkr.ecr.us-east-1.amazonaws.com/mechanics-api`.
- Imagem implantada: `deploy-20261029-da46b3d-cb239300`, construída dos arquivos locais, que ainda têm alterações sem commit.
- Digest publicado: `sha256:2a38879759bc24a93b44f9d43a91c13dd4e107b0b476073dffeaff99245915bf`.
- External Secrets Operator: chart 2.13.0, namespace `external-secrets`.
- Aplicação: Deployment, Service ClusterIP e HPA no namespace `default`.

O segredo `mechanics-api/dev/application`, que estava agendado para exclusão, foi restaurado e importado no Terraform. Como não havia versão com valores, foram geradas credenciais iniciais e armazenadas no Secrets Manager. Nenhum valor foi colocado neste documento ou em arquivos versionados. O plano aplicado, que continha a senha como dado sensível, foi removido localmente.

## Verificações executadas

- Build Docker concluído e imagem publicada no ECR.
- SecretStore `Ready=True`; ExternalSecret `SecretSynced`, `Ready=True`.
- Dois pods da API `Running`, `Ready`, sem reinícios na verificação inicial.
- Rollout concluído.
- Service acessado por port-forward: liveness e readiness responderam HTTP 200 com `status=UP`. A readiness inclui o banco.
- Logs confirmaram validação e aplicação das cinco migrations Flyway, com schema em V5.
- API de métricas `Available`; HPA `ScalingActive=True`.
- Antes do teste de carga: dois pods, aproximadamente `3m` de CPU e 305–306 MiB de memória por pod; HPA com CPU 1% e memória 59%.
- Teste sintético: carga de CPU por 120 segundos em uma réplica, sem alterações de dados. O HPA aumentou automaticamente de 2 para 4 e depois para 6 pods. A carga terminou e o processo foi encerrado pelo timeout. A redução depende da janela de estabilização de cinco minutos e das métricas, inclusive do consumo de memória da JVM.
- Validações locais: Terraform validate/fmt, actionlint, YAML, renderização real por kubectl/jq e simulações de falha de sincronização, conta divergente e imagem de outro repositório.

Uma leitura da API após o rollout falhou por resolução DNS transitória na máquina local. As consultas seguintes confirmaram aplicação e métricas saudáveis. O script passou a repetir as verificações finais de leitura até três vezes; isso não repete o provisionamento.

## Acesso e próximos deploys

O Service permanece interno. Em PowerShell:

```powershell
aws eks update-kubeconfig --profile kayo-leanndro --region us-east-1 --name mechanics-api-dev
kubectl get pods,hpa -n default -l app=mechanics-api
kubectl port-forward -n default service/mechanics-api 8080:8080
```

A API fica acessível localmente em `http://localhost:8080` enquanto o port-forward estiver ativo. O banco foi recriado; esta execução validou saúde e migrations, sem cadastrar usuário administrador ou validar login de negócio.

A pipeline foi posteriormente ajustada para ler `SPRING_DATASOURCE_PASSWORD` diretamente do Secrets Manager, sem utilizar a cópia GitHub `DB_PASSWORD`. As mudanças locais precisam ser revisadas e enviadas ao repositório para que a nova pipeline seja utilizada.

Os recursos AWS continuam ativos e sujeitos à cobrança. Consulte o [guia de deploy e escala](../k8s/deploy-eks.md) para operar o ambiente.

## Acesso público adicionado

Na mesma data, foi configurado acesso público HTTP à API e ao Swagger com AWS Load Balancer Controller 3.6.2 e Ingress `mechanics-api`. O Terraform adicionou nove recursos: role, política, associação de política, associação Pod Identity e cinco tags de sub-redes, sem alterar ou excluir os recursos existentes.

O ALB ficou `active`, com três targets `healthy`. Os testes externos retornaram HTTP 200 para Swagger e OpenAPI, e HTTP 401 para `/api/cliente` sem token. O script de deploy agora também instala o controlador, aplica o Ingress e aguarda o ALB ficar ativo antes dos testes HTTP. O acesso inicial continua disponível por port-forward como alternativa administrativa.

Endereços e operação estão no [guia do ALB](../k8s/alb.md).

## Job de migrations e rollout automático

Foi validada uma nova imagem, `migrations-20261029-0cdd31ff`, com modo `--migrate-only`. O teste integrado criou um PostgreSQL vazio, aplicou as seis migrations, confirmou a segunda execução idempotente e iniciou a API com Hibernate `validate` e Flyway desabilitado. A V6 alinha o tipo de `veiculo.ano` ao `Integer` da entidade, evitando depender de alteração automática pelo Hibernate.

No EKS, o Job `mechanics-api-migrate-2plxq` terminou `Complete`, com duração de oito segundos, validou o histórico e aplicou a V6. Somente depois disso o script atualizou o Deployment para a mesma imagem. O rollout concluiu com quatro réplicas `Ready`, sem reinícios na verificação final. O HPA continua controlando a quantidade de pods.

A role OIDC recebeu leitura somente do segredo específico e leitura das próprias políticas inline para refresh do state; não recebeu autorização para alterar as próprias permissões. As políticas IAM de preparação devem ser atualizadas pela identidade administrativa antes do CI quando precisarem mudar. O trust foi conferido para `KayoLeanndro/17SOAT-TechChallenge1`, branch `main`.

A pipeline agora preserva o artifact Docker validado entre os jobs, publica essa imagem no ECR e executa migrations antes do rollout. Os testes simulados confirmaram bloqueio do deploy quando a migration falha e isolamento do pod de migrations em relação ao Service/HPA. A execução pelo GitHub Actions ainda depende do envio das alterações locais. Consulte o [guia de migrations](../k8s/migrations.md).
