# KAP Mechanics API

API REST para o MVP de gestão de uma oficina mecânica. O sistema centraliza o cadastro de clientes, veículos, serviços, itens de estoque, orçamentos e ordens de serviço, reduzindo controles manuais e dando rastreabilidade ao atendimento.

## Objetivo

Este projeto foi desenvolvido para o **Tech Challenge — Fase 1** da FIAP. A solução aplica os conceitos de DDD, qualidade de software e segurança para apoiar o fluxo de atendimento da oficina, desde o orçamento até a entrega do veículo.

## Funcionalidades

- Cadastro e manutenção de clientes, veículos e usuários internos.
- Cadastro de serviços e itens de estoque (peças e insumos).
- Registro e consulta de movimentações de estoque.
- Geração e aprovação de orçamentos.
- Inclusão de serviços e itens de estoque no orçamento, com valores congelados.
- Criação de ordens de serviço a partir de orçamentos aprovados.
- Congelamento dos itens aprovados na O.S. e baixa transacional do estoque ao iniciar a execução.
- Acompanhamento dos status da OS: `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`.
- Persistência e consulta do histórico de status das ordens de serviço.
- Indicador de tempo médio de execução das OS por serviço.
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
| Histórico de status da OS | `GET /api/ordem-servico/{ordemServicoId}/historico-status` |
| Indicadores | `GET /api/indicadores/tempo-medio-execucao/por-servico/{servicoId}` |

### Itens do orçamento e da O.S.

`POST /api/orcamento/{id}/itens` inclui um serviço ou item de estoque em um orçamento
`PENDENTE`. Informe exatamente um de `servicoId` ou `itemEstoqueId`, além de
`quantidade`. O preço é congelado e o total do orçamento é recalculado.

`POST /api/ordem-servico/{id}/itens` inclui um serviço ou item adicional durante
uma O.S. `EM_EXECUCAO`. A inclusão de um item de estoque, inclusive os componentes
de um serviço, registra imediatamente a saída no ledger e reduz o saldo. Ao entrar
em execução, a O.S. copia os itens aprovados do orçamento e baixa seus itens de
estoque de uma só vez.

Para consultar as linhas e o status use `GET /api/orcamento/{id}/itens` ou
`GET /api/ordem-servico/{id}/itens`. Ambas as respostas trazem `status`, `itens`,
quantidade, preço unitário congelado e total da linha; a resposta da OS também
traz o vínculo `orcamentoItemId` quando a linha veio do orçamento.

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

Em seguida, acesse `http://localhost:9000` e execute a análise com um token criado na plataforma:

```powershell
.\mvnw.cmd sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<SEU_TOKEN>
```

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

As migrations do banco estão em `src/main/resources/db/migration`.
