# Migrations antes do deploy

Em pushes para `main`, o fluxo é: testes → build → teste com PostgreSQL vazio → publicação no ECR → sincronização de configurações/segredos → Job de migrations → rollout da mesma imagem nos pods → validação do ALB/Swagger.

A imagem é construída uma única vez, validada e transferida entre os jobs como artifact Docker. A publicação carrega esse artifact, sem reconstruir a aplicação. Job de migrations e Deployment recebem a mesma URI imutável no ECR.

O script `scripts/deploy-eks.sh` executa `scripts/migrate-eks.sh` antes de aplicar o Deployment. Se a migration falhar ou seu resultado não puder ser confirmado, a atualização do Deployment é bloqueada. O Flyway não executa rollback automático de mudanças já aplicadas; as migrations precisam ser compatíveis com os pods da versão anterior, que permanecem atendendo durante essa etapa.

## Job Kubernetes

`migration-job.yaml` usa a mesma imagem da API com o argumento `--migrate-only`. Esse modo executa o Flyway diretamente e encerra, sem Spring, servidor HTTP ou Hibernate. As migrations vêm de `src/main/resources/db/migration`, empacotadas na imagem.

- Cada execução cria um Job com nome único `mechanics-api-migrate-*`; reexecutar a pipeline não tenta alterar um Job imutável.
- `backoffLimit: 0` e `restartPolicy: Never`: sem repetição automática após falha.
- `activeDeadlineSeconds: 600`: limite de dez minutos.
- `ttlSecondsAfterFinished: 86400`: histórico dos Jobs/pods preservado por até 24 horas.
- O Job recebe endereço/usuário do ConfigMap e somente a senha do Secret, sem precisar da chave JWT ou token Kubernetes.
- A label do pod é diferente da API para que Service e HPA não o incluam nos destinos HTTP.

No Deployment do EKS, `SPRING_FLYWAY_ENABLED=false` e `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`. O schema é alterado pelo Job; a API confere sua compatibilidade ao iniciar. A execução local via Compose mantém o comportamento anterior de migrations na inicialização.

## Senha usada pela pipeline

O job `publish` lê `SPRING_DATASOURCE_PASSWORD` no Secrets Manager e a exporta, mascarada no GitHub Actions, como `TF_VAR_db_password`. O GitHub Secret `DB_PASSWORD` não é mais utilizado. A role OIDC recebe `secretsmanager:GetSecretValue` para o ARN específico do segredo e `iam:GetRolePolicy` para refresh das próprias políticas no state, sem poder alterar as próprias permissões. Alterações nessa política de preparação devem ser aplicadas pela identidade administrativa antes da pipeline.

O ambiente deve ter bootstrap/state inicializados e o segredo com os valores cadastrados antes dessa pipeline. Para preparar uma conta nova, aplique a infraestrutura inicialmente com a senha escolhida, cadastre a mesma senha no Secrets Manager e garanta a permissão da role. Se usar outra role OIDC, alinhe `github_actions_role_name` no Terraform e `AWS_ROLE_ARN` no GitHub.

## Acompanhamento

```powershell
kubectl get jobs -n default -l app=mechanics-api-migrations
kubectl logs -n default job/NOME_DO_JOB
kubectl describe job NOME_DO_JOB -n default
kubectl rollout status deployment/mechanics-api -n default
```

Para executar manualmente, com ConfigMap e Secret já sincronizados e a imagem publicada:

```bash
bash scripts/migrate-eks.sh URL_DO_ECR:TAG
```

Para migrar e implantar a API, use `bash scripts/deploy-eks.sh URL_DO_ECR:TAG`. Mesmo quando não há SQL novo, um novo Job valida o histórico e termina sem reaplicar migrations concluídas. Não edite arquivos de migrations já aplicados; crie uma nova versão. Não execute `repair` automaticamente em caso de falha de validação.

## Verificação do schema

O teste integrado pode ser executado localmente após construir a imagem:

```bash
python scripts/tests/test-migrations-docker.py mechanics-api:TAG
```

Ele cria PostgreSQL e rede Docker temporários sem portas públicas, executa as migrations duas vezes, confere o histórico e inicia a API com Hibernate `validate`. Os containers e volumes de teste são removidos ao terminar. Esse teste identificou a necessidade da V6, que alinha `veiculo.ano` ao tipo `Integer` da entidade, preservando os dados e mantendo V1–V5 intactas.

As mudanças precisam ser enviadas ao GitHub para que o próximo push da `main` execute esta versão da pipeline.

Referências: [Jobs Kubernetes](https://kubernetes.io/docs/concepts/workloads/controllers/job/), [API Java do Flyway](https://documentation.red-gate.com/flyway/reference/usage/api-java).
