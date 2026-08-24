# KAP - Mechanics

API para gerenciamento de oficina mecânica.

## Visão geral

O projeto expõe uma API REST para cadastro e manutenção de:

- clientes
- veículos
- usuários
- peças
- serviços
- insumos

A aplicação também conta com autenticação via JWT, documentação Swagger/OpenAPI, migrações com Flyway, persistência em PostgreSQL e análise de qualidade com SonarQube.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- SpringDoc OpenAPI
- MapStruct
- JaCoCo
- SonarQube

## Autenticação

O login é feito pelo endpoint:

- `POST /api/auth/login`

As demais rotas exigem token JWT no header:

```http
Authorization: Bearer <token>
```

O token tem duração de 1 hora e carrega as roles do usuário no claim `roles`.

## Principais endpoints

- `POST /api/auth/login`
- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`
- `POST /api/veiculo`
- `GET /api/veiculo`
- `GET /api/veiculo/{id}`
- `PUT /api/veiculo/{id}`
- `DELETE /api/veiculo/{id}`
- `POST /api/usuario`
- `GET /api/usuario`
- `GET /api/usuario/{id}`
- `PUT /api/usuario/{id}`
- `DELETE /api/usuario/{id}`
- `POST /api/peca`
- `GET /api/peca`
- `GET /api/peca/{id}`
- `PATCH /api/peca/{id}`
- `DELETE /api/peca/{id}`
- `POST /api/movimentacao-estoque/entrada`
- `POST /api/movimentacao-estoque/saida`
- `GET /api/movimentacao-estoque`
- `GET /api/movimentacao-estoque/{id}`
- `GET /api/movimentacao-estoque/item/{itemEstoqueId}`
- `GET /api/movimentacao-estoque/ordem-servico/{ordemServicoId}`
- `GET /api/movimentacao-estoque/tipo/{tipo}`
- `GET /api/movimentacao-estoque/periodo?inicio=...&fim=...`
- `PATCH /api/ordem-servico/{id}/status`
- `POST /api/servico`
- `GET /api/servico`
- `GET /api/servico/{id}`
- `PATCH /api/servico/{id}`
- `DELETE /api/servico/{id}`
- `POST /api/insumos`
- `GET /api/insumos`
- `GET /api/insumos/{id}`
- `PATCH /api/insumos/{id}`
- `DELETE /api/insumos/{id}`

## Documentação da API

A documentação Swagger fica disponível em:

- `http://localhost:8080/swagger-ui/index.html`

## Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose

## Como executar com Docker

1. Suba o banco PostgreSQL:

```bash
docker compose up -d db
```

2. Execute a aplicação:

```bash
mvn spring-boot:run
```

## Como executar localmente

Se preferir não usar Docker para subir o banco, ajuste as configurações em `src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret`

Depois execute:

```bash
mvn spring-boot:run
```

## Banco de dados

O projeto usa PostgreSQL com migrações Flyway localizadas em:

- `src/main/resources/db/migration`

## Qualidade e cobertura

O projeto já está configurado para gerar cobertura com JaCoCo e expor o relatório XML em:

- `target/site/jacoco/jacoco.xml`

Comandos úteis:

```bash
mvn test
mvn verify
```

## SonarQube

O projeto possui um serviço opcional de SonarQube no `compose.yaml`, usando o profile `quality`.

Suba o SonarQube local:

```bash
docker compose --profile quality up -d sonarqube
```

Se o container `sonarqube` já existir, remova antes de subir novamente:

```bash
docker rm -f sonarqube
```

Abra a interface em:

- `http://localhost:9000`

Crie um token na interface do SonarQube e execute a análise apontando para o servidor local:

```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<SEU_TOKEN>
```

Se necessário, informe a chave do projeto explicitamente:

```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<SEU_TOKEN> -Dsonar.projectKey=com.kap:mechanics-api
```

Observação:

- `sonar.organization` não é usado no SonarQube local. Esse parâmetro é do SonarCloud.

## Variáveis importantes

- `JWT_SECRET`: segredo usado para assinar os tokens JWT

Se a variável não for informada, a aplicação usa um valor padrão local configurado em `application.properties`.
