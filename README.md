# KAP Mechanics API

API REST para o MVP de gestão de uma oficina mecânica. O sistema centraliza o cadastro de clientes, veículos, serviços, itens de estoque, orçamentos e ordens de serviço, reduzindo controles manuais e dando rastreabilidade ao atendimento.

## Objetivo

Este projeto foi desenvolvido para o **Tech Challenge — Fase 1** da FIAP. A solução aplica os conceitos de DDD, qualidade de software e segurança para apoiar o fluxo de atendimento da oficina, desde o orçamento até a entrega do veículo.

## Funcionalidades

- Cadastro e manutenção de clientes, veículos e usuários internos.
- Cadastro de serviços e itens de estoque (peças e insumos).
- Registro e consulta de movimentações de estoque.
- Geração e aprovação de orçamentos.
- Criação de ordens de serviço a partir de orçamentos aprovados.
- Acompanhamento dos status da OS: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`.
- Autenticação JWT e controle de acesso por perfil: `ADMIN`, `ATENDENTE` e `ESTOQUISTA`.
- Documentação interativa via Swagger/OpenAPI.

## Tecnologias

- Java 21 e Spring Boot 4.1
- Spring Web, Spring Data JPA, Spring Security e Validation
- PostgreSQL 16 e Flyway
- JWT
- Docker Compose
- Swagger/OpenAPI (Springdoc)
- JUnit, JaCoCo e SonarQube

## Pré-requisitos

- Java 21
- Docker e Docker Compose

> O Maven Wrapper está incluído no projeto; não é necessário instalar o Maven globalmente.

## Como executar

1. Inicie o PostgreSQL:

   ```powershell
   docker compose up -d db
   ```

2. Defina um segredo JWT seguro (recomendado):

   ```powershell
   $env:JWT_SECRET = "uma-chave-segura-com-pelo-menos-32-caracteres"
   ```

3. Inicie a aplicação:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

As migrations do Flyway são executadas na inicialização. A API ficará disponível em `http://localhost:8080`.

### Configuração do banco

Por padrão, a aplicação usa o banco criado pelo `compose.yaml`:

| Propriedade | Valor |
| --- | --- |
| Banco | `oficina_db` |
| Usuário | `oficina_user` |
| Porta | `5432` |

Para usar outra instância PostgreSQL, ajuste as propriedades `spring.datasource.*` em `src/main/resources/application.properties`.

### Primeiro acesso local

Em uma base nova, crie o usuário administrador de desenvolvimento abaixo diretamente no PostgreSQL antes de realizar o login:

```sql
INSERT INTO usuario (nome, login, senha_hash, tipo, data_criacao)
VALUES (
    'Administrador Local',
    'admin',
    '$2a$10$Tkvd6E6bTQgy7znCFW3l9eYkH8BZKHbK2XrSw6p8pElb8tz1x1sw6',
    'ADMIN',
    CURRENT_TIMESTAMP
)
ON CONFLICT (login) DO UPDATE
SET nome = EXCLUDED.nome,
    senha_hash = EXCLUDED.senha_hash,
    tipo = EXCLUDED.tipo;
```

Credenciais locais: `admin` / `admin`. Altere ou remova esse usuário fora do ambiente de desenvolvimento.

## Autenticação e documentação da API

O login é realizado em `POST /api/auth/login` com `login` e `senha`. Use o token retornado nas rotas protegidas:

```http
Authorization: Bearer <token>
```

A documentação completa dos endpoints, payloads e respostas está disponível após iniciar a aplicação:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

As rotas de autenticação e documentação são públicas; as demais exigem JWT e respeitam as permissões de cada perfil.

## Principais recursos da API

| Recurso | Base da rota |
| --- | --- |
| Autenticação | `/api/auth` |
| Clientes | `/api/cliente` |
| Veículos | `/api/veiculo` |
| Usuários | `/api/usuario` |
| Serviços | `/api/servico` |
| Itens de estoque | `/api/item-estoque` |
| Movimentações de estoque | `/api/movimentacao-estoque` |
| Orçamentos | `/api/orcamento` |
| Ordens de serviço | `/api/ordem-servico` |

## Testes e qualidade

Execute a suíte de testes:

```powershell
.\mvnw.cmd test
```

Para executar os testes e gerar o relatório de cobertura JaCoCo:

```powershell
.\mvnw.cmd verify
```

O relatório fica em `target/site/jacoco/index.html`.

Opcionalmente, inicie o SonarQube local:

```powershell
docker compose --profile quality up -d sonarqube
```

Em seguida, acesse `http://localhost:9000`. No primeiro acesso, entre com `admin` / `admin` e altere a senha solicitada.

Antes da primeira análise, crie manualmente um projeto no SonarQube com a chave:

```text
com.kap:mechanics-api
```

Em **Project settings > Permissions**, conceda ao usuário que executará a análise a permissão **Execute Analysis**. Depois, gere um token em **My Account > Security** e execute:

```powershell
.\mvnw.cmd sonar:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=SEU_NOVO_TOKEN"
```

Substitua `SEU_NOVO_TOKEN` pelo token criado nessa instância local do SonarQube. Não use colchetes, links Markdown ou barras (`\\`) no comando. Se o projeto tiver sido criado com outra chave, informe-a explicitamente:

```powershell
.\mvnw.cmd sonar:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.projectKey=SUA_CHAVE_DO_PROJETO" "-Dsonar.token=SEU_NOVO_TOKEN"
```

> Se ocorrer o erro de autorização, confirme se a chave do projeto é `com.kap:mechanics-api` e se o token pertence a um usuário com a permissão **Execute Analysis**. Tokens expostos devem ser revogados e substituídos imediatamente.

## Análise de segurança com OWASP ZAP

O projeto também pode ser analisado com o [OWASP ZAP](https://www.zaproxy.org/), uma ferramenta de teste dinâmico de segurança (DAST). Enquanto o SonarQube analisa o código-fonte, o ZAP verifica a API em execução e gera um relatório visual com alertas, risco, evidência e recomendações.

Com o Docker Desktop em execução, inicie a API localmente. Caso o banco ainda não esteja ativo, inicie-o antes:

```powershell
docker compose up -d db
.\mvnw.cmd spring-boot:run
```

Em outro terminal, na raiz do projeto, execute a análise segura baseada na especificação OpenAPI disponível em `http://localhost:8080/v3/api-docs`:

```powershell
docker run --rm -v "${PWD}:/zap/wrk/:rw" -t ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs -f openapi -S -r zap-api-report.html -I
```

O relatório será gerado como `zap-api-report.html` na raiz do projeto. Abra-o no navegador para visualizar os achados de segurança.

### Análise autenticada com JWT

A análise anterior cobre principalmente endpoints públicos. Para incluir as rotas protegidas, obtenha um JWT de um usuário local com perfil `ADMIN` e passe-o ao ZAP no header `Authorization: Bearer`.

Em outro terminal PowerShell, com a API ainda em execução, gere o token. Substitua os valores de exemplo pelas credenciais locais e não os adicione ao repositório:

```powershell
$body = @{ login = "SEU_LOGIN_ADMIN"; senha = "SUA_SENHA_ADMIN" } | ConvertTo-Json

$token = (
  Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/api/auth/login" `
    -ContentType "application/json" `
    -Body $body
).token

$token.Length
```

Se o último comando retornar um número maior que zero, execute a análise autenticada:

```powershell
docker run --rm `
  -e "ZAP_AUTH_HEADER_VALUE=Bearer $token" `
  -e "ZAP_AUTH_HEADER_SITE=host.docker.internal" `
  -v "${PWD}:/zap/wrk/:rw" `
  -t ghcr.io/zaproxy/zaproxy:stable `
  zap-api-scan.py `
  -t http://host.docker.internal:8080/v3/api-docs `
  -f openapi `
  -S `
  -r zap-api-report-authenticated.html `
  -I
```

O relatório autenticado será gerado como `zap-api-report-authenticated.html` na raiz do projeto. A presença do JWT permite que o ZAP acesse endpoints compatíveis com o perfil usado; respostas `4xx` ainda podem ocorrer quando uma rota exigir IDs ou dados de negócio específicos.

> O parâmetro `-S` executa apenas a análise segura, sem ataques ativos. Para varredura ativa, remova esse parâmetro **somente em ambiente local ou de homologação autorizado**, pois esse modo pode enviar requisições que alteram dados.

## Estrutura do projeto

```text
src/main/java/com/kap/mechanics_api
├── controller     # Endpoints REST
├── service        # Regras de negócio
├── domain         # Entidades do domínio
├── repository     # Persistência
├── dto            # Contratos de entrada e saída
├── security       # Autenticação JWT
└── config         # Segurança e OpenAPI
```

As migrations do banco estão em `src/main/resources/db/migration`. Os documentos complementares dos fluxos de ordem de serviço e estoque estão na raiz do repositório.
