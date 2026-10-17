# Migração do contexto Cliente para Clean Architecture

Resumo do que foi feito, por que cada peça existe e o que falta. Serve de gabarito para os próximos contextos (Veículo e seguintes). Complementa o `clean-architecture-estudo.md`.

**Estado em 07/10/2026:** contexto Cliente concluído, `mvn test` com 305 testes passando.

---

## 1. Estrutura final

```
com.kap.mechanics_api
├── core/cliente/                      ← sem Spring, JPA ou DTO HTTP
│   ├── domain/    Cliente, CpfCnpj
│   ├── gateway/   ClienteGateway (interface)
│   └── usecase/   Cadastrar, Atualizar, Excluir, BuscarPorId,
│                  BuscarPorDocumento, Listar + ClienteNaoEncontradoException
├── adapter/persistence/
│   ├── entity/    ClienteJpaEntity (ex-Cliente com anotações JPA)
│   ├── ClienteJpaMapper     (Cliente <-> ClienteJpaEntity, manual)
│   └── ClienteJpaGateway    (implements ClienteGateway, usa ClienteRepository)
├── controller/ClienteController       ← traduz DTO <-> domínio e chama os use cases
├── mapper/ClienteMapper               ← MapStruct: Cliente -> DTOs de resposta
└── infra/     ClockConfig, ClienteUseCaseConfig (@Bean dos use cases)
```

---

## 2. Os seis passos e as ideias de cada um

| Passo | O que foi feito | A ideia por trás |
|---|---|---|
| 1 | Entidade JPA renomeada para `ClienteJpaEntity` e movida para `adapter/persistence/entity` | Liberar o nome `Cliente` para o domínio puro |
| 2 | `CpfCnpj` (value object) e `Cliente` (domínio puro) | Regras da oficina ficam na entidade, não no service |
| 3 | `ClienteGateway` | O core declara o que precisa; não conhece JPA |
| 4 | `ClienteJpaGateway` + `ClienteJpaMapper`; `@PrePersist` removido | O adapter implementa o contrato e traduz entre os dois modelos |
| 5 | Seis casos de uso | Um por método do service; só orquestram (buscar, chamar o domínio, salvar) |
| 6 | Controller ligado aos casos de uso; `@Bean`s na infra | O controller traduz o formato HTTP; o core nunca vê DTO |

### `static` nas fábricas do `Cliente`

- Construtor `private` + métodos `static` (`novo`, `reconstruir`) = as únicas portas de entrada. Todo `Cliente` passa pelas validações.
- `novo(...)`: cliente que ainda não existe (`id` nulo, data = agora).
- `reconstruir(...)`: cliente vindo do banco (mantém `id` e data originais).
- `atualizar(...)` é método de instância (usa `this`), e ignora valores em branco.

### Gateway: o que ele devolve

- Buscas retornam `Optional<Cliente>`. Quem decide que "não encontrado" é erro é o **caso de uso** (`orElseThrow`). Por isso a `ClienteNaoEncontradoException` mora em `core/cliente/usecase`.
- Um único `salvar(Cliente)`: o `id` decide entre INSERT e UPDATE.

### `Clock`

- Casos de uso recebem `java.time.Clock` e usam `LocalDateTime.now(clock)`.
- Em produção, o bean `Clock.systemDefaultZone()` (em `ClockConfig`). Nos testes, `Clock.fixed(...)` com UTC.

### Casos de uso: um chama outro?

- Mesmo contexto, só precisa do dado: use o **gateway** (repetir o `orElseThrow` é aceitável).
- Dispara uma ação com regra de outro contexto: aí sim chame o caso de uso dele.

---

## 3. Testes criados

| Arquivo | Tipo | O que prova |
|---|---|---|
| `core/cliente/domain/CpfCnpjTest` | unitário puro | validação, normalização e `equals`/`hashCode` |
| `core/cliente/domain/ClienteTest` | unitário puro | `novo`, `reconstruir`, `atualizar`, validações |
| `core/cliente/usecase/*UseCaseTest` (6) | Mockito, sem Spring | orquestração, 404 sem gravar, data do `Clock` fixo |
| `adapter/persistence/ClienteJpaMapperTest` | unitário puro | conversão nos dois sentidos, `id` preservado |
| `adapter/persistence/ClienteJpaGatewayIntegrationTest` | `@SpringBootTest` + H2 | ida e volta pelo banco, update sem duplicar, busca por documento |
| `ClienteControllerTest` (adaptado) | `@WebMvcTest` | contrato HTTP com os casos de uso mockados |
| `ArquiteturaTest` | ArchUnit | `core` não depende de Spring/JPA/camadas externas |

Técnicas que vale reaproveitar:
- `entityManager.flush()` + `clear()` nos testes de gateway, para forçar a leitura real do banco.
- `ArgumentCaptor` quando o caso de uso cria o objeto internamente.
- `verify(..., never())` nos caminhos de erro.
- O teste de update que conta registros protege contra o bug do `id` perdido no mapper.

---

## 4. Erros comuns encontrados (confira nos próximos contextos)

1. O `core` importando tipos de fora (DTO, `ClienteJpaGateway`, `ClockConfig`, `@Bean`). Import sem uso não é pego pelo ArchUnit (não vai para o bytecode).
2. Gateway recebendo DTOs em vez de tipos do domínio.
3. Mapper `paraEntity` sem copiar o `id` (todo update vira INSERT).
4. Construtor e `executar` do caso de uso sem `public` (a `infra` e o controller ficam em outros pacotes).
5. Injetar a classe de configuração (`ClockConfig`) em vez de `Clock`.
6. Parâmetros `String` na mesma posição trocados (telefone/e-mail).
7. Remover o `@PrePersist` quebra qualquer teste que grave a entidade sem `dataCriacao` (foi o `MovimentacaoEstoqueIntegrationTest`).
8. Em testes do ArchUnit, `..domain..` casa também com `core.cliente.domain`; use o prefixo completo.

---

## 5. O que ficou em aberto

- **`ClienteService`** reduzido a `pesquisarPorId`; `VeiculoService` e `OrcamentoService` ainda o usam (retorna `ClienteJpaEntity`). Remover quando esses contextos migrarem.
- **`OrdemServicoService`** ainda usa `ClienteRepository` direto.
- **ArchUnit**: só as regras do `core` estão ativas. As de `adapter`/`infra` e de camadas ficam para a Fase 6.
- **`NenhumCampoInformadoException`** continua em `exception/` (lançada pelo controller). Se algum caso de uso passar a lançar, sobe para o core.
- **Título do `IllegalArgumentException`** no `GlobalExceptionHandler` ("Resposta de orçamento inválida") não combina com cliente.
- **Fronteira transacional** (`@Transactional` fora do core) e o item 7 da Fase 1 do roteiro: decidir ao migrar contextos que gravam em várias tabelas (Orçamento, Ordem de Serviço).
- **`pom.xml`**: há uma alteração sua (`<source>21</source>`, `<target>21</target>`, `--enable-preview`) que não fez parte desta migração. Revise antes do commit; `--enable-preview` exige o mesmo flag em tempo de execução.
- Nada foi commitado ainda.

---

## 6. Próximo passo sugerido

Migrar **Veículo**, repetindo a receita da seção 8 do `clean-architecture-estudo.md`:

1. Domínio puro (`Veiculo`, talvez um value object `Placa`) com testes.
2. `VeiculoGateway` (só o que os casos de uso usam).
3. Renomear a entidade para `VeiculoJpaEntity` e criar gateway + mapper JPA com teste de integração.
4. Casos de uso + testes com gateway mockado.
5. Ligar o controller, `@Bean`s na infra.
6. Trocar `clienteService.pesquisarPorId` no `VeiculoService` por uma dependência do contexto Cliente (gateway ou caso de uso) e, se `OrcamentoService` já puder sair também, apagar o `ClienteService`.

Atenção ao relacionamento Cliente–Veículo (`ClienteVeiculo`, que referencia `ClienteJpaEntity`): é o ponto mais delicado dessa migração.
