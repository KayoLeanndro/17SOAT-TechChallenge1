# Permissoes de preparacao do CI/CD

A role `github-actions-mechanics-api-dev` e preparada por uma identidade administrativa na AWS. A pipeline nao recebe permissao para alterar as proprias permissoes.

- `github-actions-trust-policy.json`: trust OIDC do fork `KayoLeanndro/17SOAT-TechChallenge1`, branch `main`, com IDs imutaveis. Nao autoriza automaticamente o repositorio principal.
- `github-actions-runtime-read-policy.json`: leituras necessarias para o Terraform consultar a infraestrutura dev existente e para a pipeline consultar imagem, cluster e disponibilidade do ALB. Complementa as permissoes S3/ECR e de leitura do valor do segredo ja existentes; nao as substitui.
- `aws-load-balancer-controller-v3.6.2.json`: politica oficial do controlador, associada a sua propria role Pod Identity pelo Terraform. Nao e a politica da role GitHub Actions.

A politica de leitura pode ser adicionada como politica inline chamada `read-mechanics-dev-infrastructure` na role do GitHub Actions, depois de revisada e autorizada. O arquivo sozinho no Git nao aplica permissoes na AWS.

As consultas de rede e de load balancers sao limitadas a `us-east-1`; IAM, EKS, RDS, imagem e segredo usam ARNs definidos no arquivo. Esta politica possui apenas acoes Get/List/Describe, sem criacao, alteracao, exclusao ou PassRole.

O refresh do RDS pelo provider usa uma consulta que a AWS autoriza como `db:*`. Por isso, somente `rds:DescribeDBInstances` permite ler metadados dos bancos da conta em `us-east-1`; tags e subnet group continuam restritos aos recursos da aplicacao. O Terraform tambem consulta `secretsmanager:GetResourcePolicy` no segredo especifico para verificar sua politica de recurso, sem alterar essa politica.

Ela permite o refresh/plan da infraestrutura existente, mas nao autoriza provisionar alteracoes em EKS, RDS, IAM ou rede. Caso um plano proponha mudancas nesses recursos, revise o plano e use a identidade de infraestrutura autorizada ou uma role separada para esse provisionamento. Nao amplie a role de deploy para AdministratorAccess para contornar erros.
