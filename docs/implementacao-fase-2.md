# Manual de implementação — Fase 2

Este documento organiza a implementação e a conferência dos entregáveis da fase 2 no mesmo repositório da fase 1.

O levantamento inicial considera os arquivos locais em 26/10/2026. A presença de código ou configuração não comprova execução bem-sucedida na AWS ou no GitHub Actions. Marque as etapas de validação somente depois de executá-las.

## 1. Ponto de partida

| Item | Situação encontrada | Próxima ação |
| --- | --- | --- |
| Dockerfile | Build em múltiplas etapas com Maven e Java 21; alteração local pendente de commit. | Validar build e execução e versionar a revisão. |
| Docker Compose | `compose.yaml` com API, PostgreSQL, volume e healthcheck do banco. | Validar o ambiente completo e documentar as variáveis necessárias. |
| Contexto Docker | `.dockerignore` presente localmente, ainda não versionado no levantamento. | Revisar e incluir no commit. |
| Terraform | Bootstrap S3 e ambiente dev com ECR, EKS, IAM, rede e RDS PostgreSQL. | Validar o provisionamento e completar a documentação. |
| Kubernetes | Não foram encontrados manifestos da aplicação. | Criar os arquivos em `/k8s`, conforme o entregável. |
| HPA | `metrics-server` declarado como complemento do EKS; HPA ausente. | Validar as métricas e criar a política de escala dos pods. |
| CI/CD | Build, testes, Terraform e publicação de imagem no ECR definidos. | Adicionar deploy no EKS e verificação do rollout. |
| Banco | RDS definido no Terraform e migrations Flyway V1 a V5 presentes. | Validar aplicação das migrations e conexão da API no EKS. |
| Documentação | README com execução local e documentação de bootstrap/comandos Terraform. | Atualizar para a fase 2 e incluir arquitetura e deploy completo. |
| Arquitetura do código | Há organização por controller, service, domain, repository, DTO e config. | Definir a abordagem escolhida e conferir suas regras antes de declarar a refatoração concluída. |

## 1.1. Revisão atual dos requisitos

Esta revisão substitui o levantamento inicial como referência de progresso. **Implementado nos arquivos** não significa validado no cluster. Em 29/10/2026, após confirmar EKS e ECR inexistentes, o ambiente foi recriado e a imagem local foi implantada. Rollout, Service, probes, banco, migrations e métricas foram conferidos no EKS. Consulte o [registro do deploy real](registro-deploy-2026-10-29.md) e o [guia de deploy e escala](../k8s/deploy-eks.md). A execução da nova pipeline no GitHub continua pendente.

| Requisito original | Implementação atual | Pendência para concluir |
| --- | --- | --- |
| Dockerfile atualizado | Build em múltiplas etapas, Maven e Java 21. | Validar build e execução da versão atual e versionar as alterações. |
| Compose local | API e PostgreSQL com volume, healthcheck e dependência de inicialização. | Confirmar o funcionamento da versão atual seguindo o README. |
| Deployment | Recursos, atualização gradual e probes HTTP; réplicas controladas pelo HPA. Rollout conferido no EKS. | Confirmar os próximos rollouts pela pipeline. |
| Service | ClusterIP na porta 8080, com Ingress ALB público; Swagger/OpenAPI retornam HTTP 200 e rota protegida retorna 401 sem token. | Adicionar HTTPS quando houver domínio e certificado ACM. |
| ConfigMap | Configuração gerada com outputs reais do RDS; conexão conferida pela readiness. | Usar o script nos próximos deploys. |
| Secrets | ESO instalado; SecretStore e ExternalSecret prontos. A pipeline agora lê a senha atual do Secrets Manager com permissão IAM específica. | Executar o fluxo atualizado no GitHub após enviar os arquivos. |
| HPA por CPU/memória | `k8s/hpa.yaml` com 2 a 6 pods; métricas ativas e aumento 2→4→6 comprovado com carga sintética. | Ajustar metas com testes de carga de negócio e acompanhar a redução. |
| Terraform para cluster | EKS, dois nós Ready, IAM, complementos e regras de rede provisionados. | Manter controle do state e alterações pelo Terraform. |
| Terraform para banco | RDS disponível; cinco migrations Flyway aplicadas e conexão conferida no EKS. | Alinhar credenciais da próxima execução da pipeline. |
| Documentação dos recursos e aplicação | Guias de Terraform, Kubernetes e Secrets Manager disponíveis. | Consolidar o ambiente dev e corrigir inconsistências de backend/bootstrap. |
| Pipeline: build e testes | Maven verify e relatório JaCoCo definidos. | Confirmar execução da versão atual no GitHub Actions. |
| Pipeline: build e publicação Docker | Build e push para ECR definidos. | Publicar nova imagem com as alterações locais. |
| Pipeline: deploy do banco | Terraform aplica RDS; Job Flyway executa antes do rollout, usando a mesma imagem da API. | Confirmar a execução do workflow atualizado no GitHub. |
| Pipeline: deploy da API e aplicação dos YAMLs | Job de deploy com ESO, imagem por commit, ConfigMap gerado, Service, HPA e verificação do rollout/métricas. | Executar no GitHub Actions com acessos e segredos configurados. |

### Entregáveis adicionais

- **Mesmo repositório e caminhos exigidos:** Dockerfile, Compose, `/k8s`, `/infra` e configuração da pipeline presentes localmente. Há arquivos novos e alterações ainda sem commit.
- **Arquitetura/refatoração:** existem `core`, casos de uso, gateways, adapters e documento de migração para Clean Architecture. A conformidade de todos os contextos não foi auditada nesta revisão; não está sendo declarada concluída.
- **README:** execução local e links Swagger/OpenAPI presentes. Faltam os objetivos da fase 2, desenhos dos componentes/infraestrutura/fluxo de deploy e consolidação das instruções cloud. O texto ainda apresenta a fase 1 como objetivo.
- **Validação disponível:** os três casos de probes/autenticação passaram no teste específico executado nesta sessão. Isso não comprova a suíte inteira nem o funcionamento no EKS.

### Ordem das pendências

1. Manter a senha do RDS alinhada ao Secrets Manager, de onde a pipeline a lê para o Terraform.
2. Instalar ESO e confirmar a sincronização do Secret Kubernetes.
3. Publicar a imagem atual, preencher o Deployment e validar API/Service/RDS no EKS.
4. Testar a escala do HPA no EKS.
5. Confirmar a execução do deploy automatizado na pipeline.
6. Consolidar README, arquitetura e evidências; versionar as alterações da entrega.

## 2. Ordem de implementação

### Progresso de implementação

- [x] Criados ConfigMap, modelo de Secret e instruções em `k8s/README.md`.
- [x] Criado Deployment com recursos e probes HTTP; réplicas controladas pelo HPA.
- [x] Adicionado Actuator e liberados os dois endpoints de probes sem JWT.
- [x] Executados os três casos de `HealthProbesIntegrationTest`, sem falhas.
- [x] ConfigMap gerado pelo script a partir dos outputs do Terraform; modelo manual sem endpoint antigo.
- [x] Publicada e implantada uma imagem da versão local no ECR; credenciais alinhadas entre RDS e Secrets Manager.
- [x] Substituída a cópia GitHub DB_PASSWORD pela leitura da senha atual no Secrets Manager; permissão IAM aplicada.
- [x] Validados os manifestos no cluster e executado o deploy com conexão real ao RDS.
- [x] Criado Service ClusterIP na porta 8080.
- [x] Preparada integração Secrets Manager/ESO com Terraform, valores Helm e manifestos; guia em `k8s/secrets-manager.md`.
- [x] Provisionada a integração, cadastrados os valores na AWS, instalado o operador e confirmada a sincronização.
- [x] Implementados HPA e entrega pela pipeline, com validação local de YAML, workflow e renderização.
- [x] Confirmados rollout, métricas e aumento de réplicas com carga sintética no EKS.

Os itens marcados registram a implementação local. Os critérios de conclusão das etapas abaixo incluem a validação em execução, que permanece pendente.

Comece pelas configurações e pelos manifestos da aplicação. Depois valide um deploy manual, configure o HPA e automatize o fluxo que já foi validado. Atualize a documentação ao concluir cada etapa.

### Etapa 1 — ConfigMap e Secret

- [x] Criar `k8s/configmap.yaml` para configurações não sensíveis.
- [x] Criar `k8s/secret.yaml.example` com placeholders, sem credenciais reais.
- [ ] Definir como gerar ou aplicar o Secret real localmente e pela pipeline.
- [x] Configurar `SPRING_DATASOURCE_URL` com o endpoint do RDS, porta e nome do banco.
- [ ] Configurar o usuário do banco e injetar a senha pelo Secret.
- [ ] Injetar `JWT_SECRET` pelo Secret.
- [ ] Documentar as variáveis e a origem de cada valor.

O Compose usa `oficina_db` e `oficina_user`; o Terraform usa, por padrão, `mechanics` e `mechanics_admin`. O ambiente Kubernetes recebe os valores do RDS. A pipeline lê a senha do Secrets Manager para alimentar o Terraform; o GitHub Secret `DB_PASSWORD` não é mais utilizado. O Secret Kubernetes é sincronizado pelo External Secrets Operator, conforme `k8s/secrets-manager.md`; o arquivo local permanece como alternativa manual.

**Critério de conclusão:** o manifesto da aplicação consegue referenciar todas as configurações necessárias, e nenhum segredo real é incluído no Git. Um arquivo de exemplo não deve ser aplicado com placeholders em um ambiente real.

### Etapa 2 — Deployment da API

- [x] Criar `k8s/deployment.yaml`.
- [ ] Referenciar a imagem publicada no ECR, com tag identificável pelo commit.
- [x] Referenciar as variáveis do ConfigMap e do Secret no Deployment; carregamento em execução ainda pendente.
- [x] Configurar a porta 8080.
- [x] Definir `requests` e `limits` de CPU e memória.
- [x] Definir quantidade inicial de réplicas e estratégia de atualização.
- [x] Definir verificações de inicialização, prontidão e disponibilidade adequadas à aplicação.
- [ ] Verificar se os endpoints usados nas probes existem e podem ser acessados pelo Kubernetes.

Caso escolha endpoints do Spring Boot Actuator para as probes, implemente e configure esse suporte antes de referenciá-los no manifesto.

**Critério de conclusão:** os pods iniciam com a imagem esperada, conectam ao RDS e ficam prontos para receber tráfego.

### Etapa 3 — Service

- [x] Criar `k8s/service.yaml`.
- [x] Conferir a correspondência entre os seletores do Service e os labels do Deployment.
- [ ] Definir o tipo de acesso e documentar como chamar a API.
- [ ] Validar a rota do Service até a porta da aplicação.

**Critério de conclusão:** é possível acessar a API pelo caminho documentado. Para uma primeira validação, pode ser utilizado port-forward; a forma de acesso da entrega deve estar explícita no README.

### Etapa 4 — Deploy manual e banco de dados

- [ ] Confirmar o provisionamento do EKS, dos nós, do ECR e do RDS.
- [ ] Publicar uma imagem no ECR e usar sua referência no Deployment.
- [ ] Configurar o contexto do `kubectl` para o cluster correto.
- [ ] Criar o Secret real pelo procedimento documentado.
- [ ] Aplicar os manifestos de configuração, Deployment e Service.
- [ ] Aguardar e verificar o rollout do Deployment.
- [ ] Inspecionar pods, eventos e logs em caso de falha.
- [ ] Confirmar a conexão da API ao RDS e a execução das migrations Flyway.
- [ ] Executar um teste funcional com autenticação e leitura/escrita no banco.

O banco é provisionado pelo Terraform como RDS. No EKS, um Job executa o Flyway antes do rollout. A API inicia com Flyway desabilitado e Hibernate em `validate`, sem alterar o schema. Consulte `k8s/migrations.md`.

**Critério de conclusão:** uma versão da API funciona no EKS com dados persistidos no RDS, seguindo passos reproduzíveis.

### Etapa 5 — Horizontal Pod Autoscaler

- [ ] Criar `k8s/hpa.yaml` apontando para o Deployment da API.
- [ ] Definir mínimo e máximo de réplicas e os alvos de utilização de CPU e memória.
- [ ] Conferir os `requests` dos recursos utilizados no cálculo da utilização.
- [ ] Confirmar que o `metrics-server` está disponível e fornece métricas.
- [ ] Gerar carga controlada e observar aumento e redução de réplicas.
- [ ] Registrar a configuração e a evidência do teste.

**Critério de conclusão:** o HPA apresenta métricas válidas e ajusta réplicas conforme a política. Os limites do grupo de nós no Terraform não substituem o HPA: são configurações de recursos diferentes.

### Etapa 6 — Completar a pipeline CI/CD

O workflow atual está em `.github/workflows/tech-challenge.yml`. Ele já define testes, build Docker, provisionamento Terraform e publicação da imagem no ECR em pushes da `main`.

- [ ] Manter build e testes como condição para as etapas de entrega.
- [ ] Confirmar as Actions Variables `AWS_ROLE_ARN`, `AWS_REGION` e `TF_STATE_BUCKET`.
- [x] Cadastrados os valores no Secrets Manager e preparada sua leitura pela pipeline para o Terraform.
- [ ] Documentar os pré-requisitos da role AWS e da autenticação temporária usada pelo workflow.
- [ ] Confirmar que a role assumida tem acesso ao EKS e aos recursos necessários.
- [ ] Manter o provisionamento do cluster e do banco pelo Terraform.
- [ ] Configurar `kubectl` e obter acesso ao cluster após o provisionamento.
- [ ] Gerar as configurações do ambiente com os outputs do Terraform.
- [ ] Instalar o operador, aplicar SecretStore/ExternalSecret e aguardar a sincronização sem registrar valores nos logs.
- [ ] Aplicar os manifestos de `/k8s` com a imagem publicada pela execução atual.
- [ ] Aguardar o rollout e falhar a pipeline se a atualização não for concluída.
- [ ] Executar uma verificação básica da aplicação após o deploy.

**Critério de conclusão:** um push na `main`, após os testes passarem, provisiona ou atualiza a infraestrutura, publica a imagem e entrega a API funcional no EKS. Pull requests continuam executando as validações definidas sem realizar o deploy.

### Etapa 7 — Arquitetura e refatoração

- [ ] Registrar a abordagem de arquitetura escolhida e a justificativa.
- [ ] Definir as responsabilidades e as dependências permitidas entre os componentes.
- [ ] Revisar o código existente contra essas regras.
- [ ] Implementar as refatorações necessárias, preservando o comportamento das APIs.
- [ ] Atualizar e executar os testes afetados.
- [ ] Garantir que o desenho e a documentação representam o código entregue.

**Critério de conclusão:** o repositório contém o código atualizado e refatorado segundo a abordagem declarada, com testes pertinentes passando. A organização atual de pastas, isoladamente, não comprova esse requisito.

## 3. Estrutura esperada do repositório

```text
/
├── Dockerfile
├── .dockerignore
├── compose.yaml
├── README.md
├── k8s/
│   ├── configmap.yaml
│   ├── secret.yaml.example
│   ├── deployment.yaml
│   ├── service.yaml
│   └── hpa.yaml
├── infra/
│   └── terraform/
│       ├── bootstrap/
│       └── environments/dev/
├── .github/workflows/
│   └── tech-challenge.yml
├── docs/
│   ├── implementacao-fase-2.md
│   ├── architecture/
│   └── terraform/
└── src/
```

`compose.yaml` já é o arquivo Docker Compose do projeto; não é necessário renomeá-lo para `docker-compose.yml`. Os manifestos devem ficar em `/k8s`, conforme o entregável da fase 2, substituindo a sugestão anterior de `infra/kubernetes/`.

## 4. Documentação e ajustes conhecidos

- [ ] Corrigir `infra/terraform/environments/dev/backend.tf.example`: a chave deve ser `environments/dev/terraform.tfstate`, sem reutilizar a chave do bootstrap.
- [ ] Alinhar a documentação de bootstrap ao repositório: ela diz que `backend.tf` não é versionado, mas o arquivo está versionado.
- [ ] Explicar como criar o bucket na primeira execução, quando ele ainda não existe e o backend S3 não pode ser usado.
- [ ] Atualizar `docs/terraform/comandos-terraform.md`, que ainda descreve dev como ECR e recursos futuros.
- [ ] Listar os recursos provisionados: bucket S3 de estado, ECR e retenção de imagens, EKS, grupo de nós, metrics-server, roles/permissões, configurações de acesso, RDS, grupo de sub-redes e regras de rede.
- [ ] Explicar que a configuração reutiliza a VPC padrão e suas sub-redes.
- [ ] Documentar variáveis, outputs, aplicação, verificação e remoção dos recursos.

## 5. Checklist dos entregáveis da fase 2

### Repositório Git — o mesmo da fase 1

- [ ] Código-fonte atualizado e refatorado conforme as boas práticas da arquitetura escolhida.
- [ ] Dockerfile revisado e validado.
- [ ] Docker Compose revisado e validado.
- [ ] Manifestos Kubernetes em `/k8s`.
- [ ] Scripts Terraform em `/infra`.
- [ ] Arquivos de configuração da pipeline CI/CD versionados.
- [ ] Alterações necessárias incluídas nos commits da entrega.

### README.md

- [ ] Descrição da solução e dos objetivos da fase 2.
- [ ] Desenho da arquitetura proposta com os componentes da aplicação.
- [ ] Desenho com a infraestrutura provisionada.
- [ ] Desenho ou explicação visual do fluxo de deploy.
- [ ] Instruções para execução local.
- [ ] Instruções para deploy em Kubernetes.
- [ ] Instruções para provisionamento da infraestrutura com Terraform.
- [ ] Link para a documentação completa das APIs: collection Postman, Swagger/OpenAPI ou similar.

O README atual já informa `/swagger-ui/index.html` e `/v3/api-docs` para execução local. Confira se a especificação cobre todas as APIs e forneça instruções claras de acesso, incluindo autenticação quando necessária. Se usar uma collection, mantenha seu link ou arquivo acessível e sem tokens reais.

### Validação final

- [ ] Ambiente local funciona seguindo somente o README.
- [ ] Infraestrutura pode ser provisionada seguindo a documentação.
- [ ] Pipeline passa pelos testes e conclui o deploy.
- [ ] Pods ficam prontos e a API responde pelo acesso documentado.
- [ ] API persiste dados no banco provisionado.
- [ ] HPA recebe métricas e seu comportamento foi verificado.
- [ ] Código, manifestos, infraestrutura e desenhos descrevem a mesma solução.

## 6. Primeiro passo prático

Inicie pela **Etapa 1**, criando `k8s/configmap.yaml` e `k8s/secret.yaml.example`. Em seguida, implemente Deployment e Service e valide o deploy manual antes de automatizá-lo na pipeline.
