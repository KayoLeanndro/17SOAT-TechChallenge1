# KAP Mechanics API

Backend REST para gestao de uma oficina mecanica, desenvolvido como MVP do Tech Challenge. A aplicacao centraliza os cadastros administrativos da oficina e oferece uma base segura e organizada para atendimento, catalogo de servicos, controle de pecas, insumos e usuarios internos.

## Visao Geral

O KAP Mechanics API foi pensado para apoiar a operacao de uma oficina mecanica de medio porte, substituindo controles manuais e planilhas por uma API monolitica em camadas. O projeto usa autenticacao JWT, persistencia relacional com PostgreSQL, migrations com Flyway e documentacao interativa via Swagger/OpenAPI.

O backend disponibiliza recursos para:

- Gerenciar clientes.
- Gerenciar veiculos.
- Gerenciar usuarios administrativos.
- Gerenciar servicos oferecidos pela oficina.
- Gerenciar pecas e seus dados de estoque.
- Gerenciar insumos e seus dados de estoque.
- Proteger endpoints administrativos com JWT e roles.
- Versionar o schema do banco de dados com Flyway.
- Consultar e testar endpoints pela interface Swagger.

## Stack Tecnica

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT
- PostgreSQL 16
- Flyway
- MapStruct
- Maven
- Docker Compose
- Springdoc OpenAPI
- JUnit
- Mockito

## Arquitetura

O projeto segue uma arquitetura monolitica em camadas, separando responsabilidades de entrada HTTP, regra de aplicacao, persistencia, seguranca e contratos de API.

```text
src/main/java/com/kap/mechanics_api
├── config          # Configuracoes da aplicacao e seguranca
├── controller      # Endpoints REST
├── documentation   # Contratos auxiliares para Swagger/OpenAPI
├── domain          # Entidades JPA
├── dto             # Objetos de entrada e saida da API
├── enums           # Enumeracoes de dominio
├── exception       # Excecoes de negocio
├── infra           # Tratamento global de erros
├── mapper          # Conversao entre entidades e DTOs
├── repository      # Repositorios Spring Data JPA
├── security        # Servicos de autenticacao e JWT
└── service         # Casos de uso e regras de aplicacao
```

Essa organizacao facilita manutencao, testes e evolucao do dominio sem acoplar regras de negocio diretamente aos controllers.

## Banco de Dados

O banco de dados utilizado e o PostgreSQL.

A escolha foi feita por se tratar de um banco relacional robusto, adequado para sistemas transacionais e para um dominio com relacoes importantes entre clientes, veiculos, servicos, pecas, usuarios e registros operacionais da oficina. O PostgreSQL oferece integridade referencial, constraints, indices, tipos enumerados e suporte consistente a transacoes ACID.

As migrations ficam em:

```text
src/main/resources/db/migration
```

Ao iniciar a aplicacao, o Flyway aplica automaticamente as migrations disponiveis no banco configurado.

## Requisitos

- JDK 21
- Docker
- Docker Compose
- Maven Wrapper incluido no projeto

Confirme a versao do Java:

```bash
java -version
```

O projeto deve ser executado com Java 21.

## Configuracao

As configuracoes padrao da aplicacao estao em:

```text
src/main/resources/application.properties
```

Valores principais:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/oficina_db
spring.datasource.username=oficina_user
spring.datasource.password=oficina_pass
jwt.secret=${JWT_SECRET:chave-local-mvp-123456789012345678901234567890}
```

Para informar um segredo JWT proprio:

```bash
export JWT_SECRET="sua-chave-secreta"
```

## Executando o Projeto

Suba o PostgreSQL com Docker Compose:

```bash
docker compose up -d
```

Execute a aplicacao:

```bash
./mvnw spring-boot:run
```

Caso o wrapper nao tenha permissao de execucao:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Ou execute diretamente com Bash:

```bash
bash mvnw spring-boot:run
```

A API estara disponivel em:

```text
http://localhost:8080
```

## Ambiente Docker

O `compose.yaml` provisiona o banco PostgreSQL usado pela aplicacao em ambiente local.

| Servico | Valor |
| --- | --- |
| Container | `oficina-db` |
| Imagem | `postgres:16` |
| Database | `oficina_db` |
| Usuario | `oficina_user` |
| Senha | `oficina_pass` |
| Porta | `5432` |

Comandos uteis:

```bash
docker compose up -d
docker compose ps
docker compose logs -f db
docker compose down
```

## Swagger/OpenAPI

Com a aplicacao em execucao, acesse a documentacao interativa:

```text
http://localhost:8080/swagger-ui/index.html
```

Contrato OpenAPI em JSON:

```text
http://localhost:8080/v3/api-docs
```

## Autenticacao

O endpoint de login retorna um token JWT para uso nas rotas protegidas.

```http
POST /api/auth/login
```

Exemplo:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "senha": "admin"
  }'
```

Resposta:

```json
{
  "token": "eyJ..."
}
```

Use o token no header `Authorization`:

```bash
curl http://localhost:8080/api/cliente \
  -H "Authorization: Bearer SEU_TOKEN"
```

## Perfis de Acesso

| Role | Permissoes |
| --- | --- |
| `ADMIN` | Acesso administrativo amplo, incluindo usuarios |
| `ATENDENTE` | Acesso a clientes e veiculos |
| `ESTOQUISTA` | Acesso a servicos, pecas e insumos |

## Endpoints

### Autenticacao

| Metodo | Rota | Descricao |
| --- | --- | --- |
| POST | `/api/auth/login` | Autentica usuario e retorna JWT |

### Usuarios

| Metodo | Rota | Descricao | Role |
| --- | --- | --- | --- |
| POST | `/api/usuario` | Cria usuario | `ADMIN` |
| GET | `/api/usuario` | Lista usuarios | `ADMIN` |
| GET | `/api/usuario/{id}` | Busca usuario por ID | `ADMIN` |
| PUT | `/api/usuario/{id}` | Atualiza usuario | `ADMIN` |
| DELETE | `/api/usuario/{id}` | Remove usuario | `ADMIN` |

### Clientes

| Metodo | Rota | Descricao | Roles |
| --- | --- | --- | --- |
| POST | `/api/cliente` | Cria cliente | `ADMIN`, `ATENDENTE` |
| GET | `/api/cliente` | Lista clientes | `ADMIN`, `ATENDENTE` |
| GET | `/api/cliente/{id}` | Busca cliente por ID | `ADMIN`, `ATENDENTE` |
| PUT | `/api/cliente/{id}` | Atualiza cliente | `ADMIN`, `ATENDENTE` |
| DELETE | `/api/cliente/{id}` | Remove cliente | `ADMIN`, `ATENDENTE` |

### Veiculos

| Metodo | Rota | Descricao | Roles |
| --- | --- | --- | --- |
| POST | `/api/veiculo` | Cria veiculo | `ADMIN`, `ATENDENTE` |
| GET | `/api/veiculo` | Lista veiculos | `ADMIN`, `ATENDENTE` |
| GET | `/api/veiculo/{id}` | Busca veiculo por ID | `ADMIN`, `ATENDENTE` |
| PUT | `/api/veiculo/{id}` | Atualiza veiculo | `ADMIN`, `ATENDENTE` |
| DELETE | `/api/veiculo/{id}` | Remove veiculo | `ADMIN`, `ATENDENTE` |

### Servicos

| Metodo | Rota | Descricao | Roles |
| --- | --- | --- | --- |
| POST | `/api/servico` | Cria servico | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/servico` | Lista servicos | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/servico/{id}` | Busca servico por ID | `ADMIN`, `ESTOQUISTA` |
| PATCH | `/api/servico/{id}` | Atualiza servico | `ADMIN`, `ESTOQUISTA` |
| DELETE | `/api/servico/{id}` | Remove servico | `ADMIN`, `ESTOQUISTA` |

### Pecas

| Metodo | Rota | Descricao | Roles |
| --- | --- | --- | --- |
| POST | `/api/peca` | Cria peca | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/peca` | Lista pecas | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/peca/{id}` | Busca peca por ID | `ADMIN`, `ESTOQUISTA` |
| PATCH | `/api/peca/{id}` | Atualiza peca | `ADMIN`, `ESTOQUISTA` |
| DELETE | `/api/peca/{id}` | Remove peca | `ADMIN`, `ESTOQUISTA` |

### Insumos

| Metodo | Rota | Descricao | Roles |
| --- | --- | --- | --- |
| POST | `/api/insumos` | Cria insumo | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/insumos` | Lista insumos | `ADMIN`, `ESTOQUISTA` |
| GET | `/api/insumos/{id}` | Busca insumo por ID | `ADMIN`, `ESTOQUISTA` |
| PATCH | `/api/insumos/{id}` | Atualiza insumo | `ADMIN`, `ESTOQUISTA` |
| DELETE | `/api/insumos/{id}` | Remove insumo | `ADMIN`, `ESTOQUISTA` |

## Exemplos de Uso

### Criar cliente

```bash
curl -X POST http://localhost:8080/api/cliente \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Silva",
    "cpfCnpj": "12345678901",
    "telefone": "11999999999",
    "email": "maria@email.com"
  }'
```

### Criar veiculo

```bash
curl -X POST http://localhost:8080/api/veiculo \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC1D23",
    "marca": "Toyota",
    "modelo": "Corolla",
    "ano": 2020
  }'
```

### Criar servico

```bash
curl -X POST http://localhost:8080/api/servico \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Troca de oleo",
    "descricao": "Substituicao do oleo do motor",
    "valorMaoObra": 80.00,
    "tempoEstimadoMin": 30,
    "ativo": true
  }'
```

### Criar peca

```bash
curl -X POST http://localhost:8080/api/peca \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Filtro de oleo",
    "descricao": "Filtro compativel com motor 1.6",
    "valorUnitario": 35.90,
    "quantidadeAtual": 20,
    "quantidadeMinima": 5,
    "ativo": true
  }'
```

### Criar insumo

```bash
curl -X POST http://localhost:8080/api/insumos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Oleo 5W30",
    "descricao": "Oleo sintetico para motor",
    "valorUnitario": 42.50,
    "quantidadeAtual": 30,
    "quantidadeMinima": 8,
    "ativo": true
  }'
```

## Testes

Execute a suite automatizada com:

```bash
./mvnw test
```

Para ambientes com mais de uma versao de Java instalada:

```bash
JAVA_HOME=/caminho/para/jdk-21 ./mvnw test
```

## Estrutura do Repositorio

```text
.
├── compose.yaml
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java/com/kap/mechanics_api
│   │   └── resources
│   │       ├── application.properties
│   │       └── db/migration
│   └── test
│       └── java/com/kap/mechanics_api
```

## Documentacao da API

A forma recomendada de explorar os contratos REST e executar chamadas manuais e pela interface Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```
