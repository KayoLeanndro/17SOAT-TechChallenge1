# Registro de implementação — 26/10/2026

## Objetivo do trabalho

Preparar a mechanics-api para execução no Kubernetes, organizar os requisitos da fase 2 e iniciar a integração das credenciais com o AWS Secrets Manager.

Este registro descreve o que foi adicionado ou ajustado durante a sessão, por que cada mudança existe e quais verificações foram realizadas. Configuração escrita, planejamento do Terraform e recurso funcionando na AWS são etapas distintas. Não foi apresentado resultado conclusivo de `terraform apply` ou `terraform destroy` ao final da sessão.

Nenhuma senha, chave JWT ou conteúdo do Secret local é reproduzido neste documento.

## 1. O que já existia antes

Os itens abaixo foram encontrados no projeto; não foram criados do zero nesta sessão:

| Item | Finalidade |
| --- | --- |
| Dockerfile com múltiplas etapas | Compilar a API com Maven/Java 21 e executá-la em uma imagem JRE. |
| `compose.yaml` | Executar API e PostgreSQL no desenvolvimento local, com volume e healthcheck do banco; inclui SonarQube opcional. |
| Terraform do bootstrap | Criar o bucket S3 de estado, com bloqueio de acesso público, criptografia, versionamento e proteção contra destruição. |
| Terraform do ambiente dev | Definir ECR, RDS PostgreSQL, EKS, nós, IAM, complementos e regras de acesso ao banco. |
| Workflow do GitHub Actions | Executar Maven verify, gerar relatório JaCoCo, construir imagem, aplicar Terraform e publicar no ECR. |
| Migrations Flyway | Evoluir o esquema do banco na inicialização da aplicação. |
| Swagger/OpenAPI | Documentar as APIs e permitir sua consulta durante a execução. |
| Estrutura de Clean Architecture | Código em core, casos de uso, gateways e adapters, com documentação de migração existente. |

O Dockerfile já tinha alterações locais no início do trabalho. Sua versão atual usa o Maven instalado na imagem de build, sem copiar o Maven Wrapper. O `.dockerignore`, também já presente como arquivo local não versionado no levantamento inicial, exclui `.git`, `target`, `.mvn` e `.m2`. Esses itens foram inspecionados, mas não devem ser confundidos com novas implementações feitas nesta sessão. O Compose e o workflow não receberam novas etapas durante este trabalho.

## 2. Manifestos Kubernetes criados

### ConfigMap

**Arquivo:** [k8s/configmap.yaml](../k8s/configmap.yaml).

Foi criado o ConfigMap `mechanics-api-config`, contendo:

- `SPRING_DATASOURCE_URL`;
- `SPRING_DATASOURCE_USERNAME`;
- `SERVER_PORT`.

**Por que foi criado:** separar as configurações do ambiente da imagem Docker. O Spring Boot recebe essas variáveis e substitui os valores correspondentes do `application.properties`. Assim, a mesma imagem pode ser usada com o PostgreSQL local ou com o RDS.

O usuário preencheu o endpoint do RDS na URL JDBC. Isso configura o destino da conexão, mas não comprova que a aplicação já conseguiu conectar ao banco pelo EKS.

### Modelo e cópia local de Secret

**Modelo versionável:** [k8s/secret.yaml.example](../k8s/secret.yaml.example).

**Arquivo local:** `k8s/secret.local.yaml`, ignorado pelo Git.

Foi preparado o Secret `mechanics-api-secrets`, com os campos:

- `SPRING_DATASOURCE_PASSWORD`;
- `JWT_SECRET`.

O arquivo usa `type: Opaque` e `stringData`, permitindo informar valores como texto sem conversão manual para base64. A cópia local foi criada com uma chave JWT aleatória de 32 bytes; a senha do banco foi preenchida pelo usuário no editor.

**Por que foi criado:** fornecer dados sensíveis ao container sem gravá-los no ConfigMap ou nos arquivos versionados. Todas as réplicas precisam compartilhar a mesma chave JWT para validar os tokens emitidos pela aplicação.

Posteriormente, foi escolhida a integração com Secrets Manager como fluxo principal. O arquivo local permanece como alternativa manual e não deve ser aplicado em paralelo ao operador que gerencia o mesmo Secret.

### Deployment

**Arquivo:** [k8s/deployment.yaml](../k8s/deployment.yaml).

Foi criado o Deployment `mechanics-api`, com:

| Configuração | Valor atual | Finalidade |
| --- | --- | --- |
| Réplicas | 3 | Manter três instâncias da API. O manifesto começou com duas e foi ajustado pelo usuário. |
| Porta do container | 8080, nomeada `http` | Conectar probes e Service à porta da aplicação. |
| Configurações | `envFrom` com ConfigMap e Secret | Injetar as variáveis no processo Java. |
| Requests | CPU `250m` e memória `512Mi` por container | Informar ao agendador a capacidade necessária e preparar referências para o futuro HPA. |
| Limits | CPU `1` e memória `1Gi` por container | Limitar o consumo de recursos. |
| Estratégia | `RollingUpdate`, `maxSurge: 1`, `maxUnavailable: 0` | Substituir réplicas gradualmente, aguardando a prontidão das novas. |
| Histórico | 3 revisões | Limitar o histórico de ReplicaSets mantido pelo Deployment. |
| Prazo de progresso | 600 segundos | Detectar uma atualização que não está avançando. |

**Por que foi criado:** declarar como o Kubernetes deve iniciar, manter e atualizar os containers da API.

Os valores de CPU e memória são iniciais, não resultados de testes de carga. A configuração atual também não garante que cada réplica seja agendada em um nó diferente.

**Pendência:** o campo `image` ainda contém marcadores. É necessário publicar uma imagem que inclua o Actuator e preencher a referência completa do ECR com a tag correspondente antes do deploy.

### Service

**Arquivo:** [k8s/service.yaml](../k8s/service.yaml).

O usuário preencheu o Service `mechanics-api`, do tipo `ClusterIP`, com porta 8080, `targetPort: http` e seletor `app: mechanics-api`, correspondente aos labels do Deployment.

**Por que foi criado:** oferecer um endereço estável dentro do cluster e encaminhar requisições para os pods prontos. Os endereços individuais dos pods podem mudar quando eles são substituídos.

Esse Service não cria um endereço público externo. O acesso externo definitivo ainda precisa ser escolhido; o guia prevê port-forward para a validação inicial.

## 3. Endpoints de saúde e probes

**Arquivos alterados:**

- [pom.xml](../pom.xml): inclusão de `spring-boot-starter-actuator`.
- [application.properties](../src/main/resources/application.properties): exposição de health, habilitação das probes e configuração dos grupos.
- [SecurityConfig.java](../src/main/java/com/kap/mechanics_api/config/SecurityConfig.java): liberação dos dois caminhos de probes sem JWT.

| Probe | Endpoint | Comportamento esperado |
| --- | --- | --- |
| Startup | `/actuator/health/liveness` | Conceder aproximadamente cinco minutos para a inicialização antes de reiniciar o container. |
| Readiness | `/actuator/health/readiness` | Verificar se a API está pronta, incluindo a saúde do banco; retirar pods não prontos do tráfego do Service. |
| Liveness | `/actuator/health/liveness` | Verificar o estado da aplicação e reiniciar o container após falhas consecutivas, sem usar o banco como critério. |

**Por que foram criadas:** permitir que o Kubernetes diferencie uma aplicação ainda iniciando, uma réplica temporariamente incapaz de atender e um processo que precisa ser reiniciado.

O banco foi incluído na readiness porque é necessário às operações da API. Ele não foi incluído na liveness para evitar reinícios em massa causados apenas por uma indisponibilidade do RDS. Enquanto a startup probe não tiver sucesso, as outras duas ficam suspensas.

Os endpoints não exibem detalhes internos. A liberação de autenticação foi limitada aos dois caminhos das probes; o health geral e as rotas protegidas da API continuam exigindo autenticação.

## 4. Integração com AWS Secrets Manager

Foi definida a seguinte sequência:

```text
AWS Secrets Manager: mechanics-api/dev/application
    -> External Secrets Operator autenticado via EKS Pod Identity
    -> Secret Kubernetes: mechanics-api-secrets
    -> Variáveis de ambiente do container da API
```

**Por que foi criada:** centralizar as credenciais na AWS e permitir que o cluster obtenha os valores sem depender da aplicação manual de um arquivo local e sem chaves AWS estáticas nos manifestos.

### Recursos adicionados ao Terraform

**Arquivo:** [infra/terraform/environments/dev/secrets.tf](../infra/terraform/environments/dev/secrets.tf).

| Recurso | Por que existe |
| --- | --- |
| `aws_secretsmanager_secret.application` | Criar os metadados do segredo da aplicação, com janela de recuperação de sete dias na exclusão. |
| `aws_eks_addon.pod_identity_agent` | Disponibilizar o agente necessário ao EKS Pod Identity nos nós. |
| `aws_iam_role.external_secrets` | Definir a identidade AWS do operador, com trust policy restrita ao namespace e ServiceAccount esperados. |
| `aws_iam_role_policy.external_secrets` | Permitir somente `GetSecretValue` e `DescribeSecret` no ARN exato do segredo da aplicação. |
| `aws_eks_pod_identity_association.external_secrets` | Associar a role ao ServiceAccount `external-secrets`, no namespace `external-secrets`. |

Também foram adicionados:

- Em [variables.tf](../infra/terraform/environments/dev/variables.tf), `application_secret_name`, com padrão `mechanics-api/dev/application`.
- Em [outputs.tf](../infra/terraform/environments/dev/outputs.tf), nome e ARN do segredo e ARN da role do operador, sem os valores das credenciais.

O Terraform novo gerencia apenas os metadados do segredo. Não foi adicionado `aws_secretsmanager_secret_version`. Os valores devem ser cadastrados no Secrets Manager conforme o guia. A senha do recurso RDS existente continua configurada por `var.db_password`; a integração não remove automaticamente esse valor do estado Terraform do banco.

### Operador e manifestos de sincronização

| Arquivo | O que define e por quê |
| --- | --- |
| [infra/helm/external-secrets/values.yaml](../infra/helm/external-secrets/values.yaml) | Instalação das CRDs, nome do ServiceAccount alinhado ao Pod Identity e reconciliação limitada ao namespace `default`. |
| [k8s/secret-store.yaml](../k8s/secret-store.yaml) | SecretStore `mechanics-api-aws`, apontando para Secrets Manager em `us-east-1`, usando a identidade do operador. |
| [k8s/external-secret.yaml](../k8s/external-secret.yaml) | Mapeamento dos dois campos do segredo AWS para o Secret `mechanics-api-secrets`, com consulta periódica a cada hora. |

O Deployment mantém o mesmo `secretRef`. Ele não precisa conhecer a API do Secrets Manager.

**Limites da implementação:** os arquivos não comprovam que o operador foi instalado ou que os valores foram sincronizados. O Helm não estava disponível no computador durante a preparação. Atualizar o Secret não atualiza variáveis de processos já iniciados: é necessário substituir os pods após confirmar a sincronização. A troca da chave JWT também afeta tokens já emitidos.

## 5. Proteção dos arquivos locais

O [.gitignore](../.gitignore) foi atualizado para ignorar:

- `k8s/secret.local.yaml`;
- arquivos locais `k8s/*.secret.env`;
- `infra/terraform/environments/dev/tfpla`, nome usado em um dos planos salvos.

**Por que foi alterado:** impedir o versionamento acidental de arquivos locais com credenciais ou planos Terraform que podem conter dados sensíveis. O padrão `tfplan` já estava ignorado; `tfpla`, sem o último caractere, precisava de uma regra específica.

As regras foram verificadas com `git check-ignore`.

## 6. Documentação criada e atualizada

| Documento | Finalidade |
| --- | --- |
| [Manual da fase 2](implementacao-fase-2.md) | Organizar requisitos, ordem de implementação, critérios de conclusão, checklist e progresso. |
| [Guia Kubernetes](../k8s/README.md) | Explicar os manifestos, variáveis, probes e comandos de validação e aplicação. |
| [Guia Secrets Manager](../k8s/secrets-manager.md) | Explicar provisionamento, cadastro dos valores, instalação do operador, sincronização e diagnóstico. |
| [README principal](../README.md) | Incluir a descrição da integração e o acesso ao guia detalhado. |
| Este registro | Preservar as decisões e a justificativa do trabalho realizado em 26/10/2026. |

O checklist foi revisado para distinguir arquivos implementados de comportamentos ainda não verificados em execução.

## 7. Verificações realizadas

### Testes da aplicação

Foi criado [HealthProbesIntegrationTest.java](../src/test/java/com/kap/mechanics_api/config/HealthProbesIntegrationTest.java), usando o perfil de testes com H2.

Comando executado:

```powershell
mvn -Dtest=HealthProbesIntegrationTest test
```

Resultado: **3 casos executados, zero falhas e zero erros**.

Os casos verificam que liveness e readiness respondem sem JWT, não exibem os componentes internos e que o health geral e uma rota protegida da API continuam retornando 401 sem autenticação.

Isso valida o comportamento HTTP e de segurança no ambiente de testes. Não comprova acesso ao RDS, execução das migrations no EKS, desempenho ou passagem da suíte completa.

### Terraform e YAML

- `terraform fmt -check` passou na configuração de dev após formatação do arquivo novo.
- `terraform validate` passou.
- Os YAMLs do SecretStore, ExternalSecret e valores Helm foram lidos com um parser YAML sem erro de sintaxe.
- `git diff --check` não identificou erros de whitespace nos arquivos rastreados verificados.

Esses resultados não substituem a validação das CRDs no cluster nem a instalação real do chart Helm.

### Plano de infraestrutura e diagnóstico

O usuário executou o plano do Terraform, cujo resultado compartilhado foi:

```text
Plan: 11 to add, 1 to change, 0 to destroy.
```

As onze criações incluíam os cinco recursos novos da integração e seis recursos já descritos no código existente: cluster, grupo de nós, metrics-server, entrada de acesso, associação de política de acesso e regra de entrada no PostgreSQL.

A alteração era a senha do RDS. Foi confirmada uma diferença entre o valor anterior registrado e o valor planejado, sem exibir as senhas. O usuário decidiu usar a senha informada no novo plano. Essa decisão precisa ser refletida também no GitHub Secret `DB_PASSWORD` e no valor usado pela API no Secrets Manager, após a aplicação correspondente.

O backend de dev foi identificado na chave `environments/dev/terraform.tfstate`, separada do bootstrap. O usuário inicializou o backend na pasta atual e conseguiu consultar o endpoint do RDS. Uma consulta compartilhada inicialmente retornou que o cluster EKS não existia naquela conta/região.

Mais tarde, o usuário relatou dez pods, mas a listagem não pôde ser obtida: `kubectl get pods -A` retornou erro de autenticação. Não foi confirmado quais workloads compunham essa contagem. Foram orientadas a conferência da identidade AWS e a atualização do kubeconfig. A existência de componentes de sistema explica por que a contagem total de pods pode superar o número de réplicas da API, mas não foi comprovada a distribuição neste cluster.

## 8. Solicitação de remoção do ambiente dev

Ao final, o usuário solicitou `terraform destroy` do ambiente dev. Foram conferidos os arquivos do backend e orientados os comandos de planejamento e remoção na pasta correta.

A remoção prevista abrange os recursos gerenciados pelo estado de dev. O bootstrap é separado. O RDS está configurado com `skip_final_snapshot = true` e sem proteção contra exclusão, portanto sua remoção não cria um snapshot final.

O destroy não foi executado pelo assistente: a identidade AWS disponível ao processo era diferente da conta usada pelo usuário. **Não foi recebido resultado de destroy concluído.** Este registro não afirma que os recursos foram removidos ou que permanecem ativos; o estado atual deve ser conferido na conta correta antes da próxima operação.

## 9. O que ficou pendente

- [ ] Confirmar o estado final do ambiente dev após a solicitação de destroy.
- [ ] Quando for retomar o ambiente, confirmar o provisionamento e o acesso ao EKS.
- [ ] Cadastrar ou confirmar os valores no Secrets Manager e alinhar a senha com RDS e GitHub Actions.
- [ ] Instalar o External Secrets Operator e validar a sincronização.
- [ ] Publicar uma imagem com Actuator e substituir o marcador no Deployment.
- [ ] Aplicar os manifestos e validar API, Service, banco e migrations.
- [ ] Criar `k8s/hpa.yaml`, conferir métricas e testar escala por CPU/memória.
- [ ] Completar o workflow com deploy no EKS, aplicação dos YAMLs e verificação do rollout.
- [ ] Consolidar os objetivos da fase 2 e os desenhos de arquitetura, infraestrutura e deploy no README.
- [ ] Corrigir inconsistências já identificadas na documentação de bootstrap e no exemplo de backend de dev.
- [ ] Auditar a conformidade da refatoração arquitetural e executar as verificações pertinentes.
- [ ] Incluir nos commits os arquivos e alterações que compõem a entrega, preservando os segredos fora do Git.

## Referências técnicas utilizadas

- [ConfigMaps](https://kubernetes.io/docs/concepts/configuration/configmap/) e [Secrets](https://kubernetes.io/docs/concepts/configuration/secret/) no Kubernetes.
- [Probes de inicialização, prontidão e disponibilidade](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/).
- [Configuração externa do Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html) e [Actuator](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html).
- [Autenticação AWS no External Secrets Operator](https://external-secrets.io/latest/provider/aws-access/) e [provider Secrets Manager](https://external-secrets.io/latest/provider/aws-secrets-manager/).
- [Trust policy do EKS Pod Identity](https://docs.aws.amazon.com/eks/latest/userguide/pod-id-role.html).
- [Configuração do kubeconfig para o EKS](https://docs.aws.amazon.com/eks/latest/userguide/create-kubeconfig.html).
