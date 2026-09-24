# Relatório de Análise de Vulnerabilidades

## Identificação

| Item | Valor |
| --- | --- |
| Projeto | KAP Mechanics API |
| Contexto | Tech Challenge — Fase 1 — FIAP |
| Data da análise | 30/08/2026 |
| Ambiente | Local, com Docker Compose |
| Escopo | API REST documentada em `/v3/api-docs` |

## Ferramentas e metodologia

Foram utilizadas duas abordagens complementares:

- **SAST:** SonarQube, para análise estática do código-fonte.
- **DAST:** OWASP ZAP API Scan, para análise dinâmica da API em execução a partir da especificação OpenAPI.

O ZAP foi executado em modo seguro (`-S`), sem ataques ativos e exclusivamente em ambiente local autorizado. Foram realizados scans sem autenticação e com JWT de um usuário de perfil `ADMIN`, conforme os comandos descritos no README.

## Resultados do OWASP ZAP

| Scan | Alta | Média | Baixa | Informativa |
| --- | ---: | ---: | ---: | ---: |
| Sem autenticação | 0 | 0 | 1 | 2 |
| Autenticado com JWT | 0 | 0 | 1 | 2 |

### Achados e tratamento

| Achado | Severidade | Evidência | Impacto | Tratamento |
| --- | --- | --- | --- | --- |
| `Cross-Origin-Resource-Policy Header Missing or Invalid` | Baixa | Presente nos dois scans | Em contextos web, a ausência do cabeçalho pode reduzir a proteção contra alguns ataques de canal lateral entre origens. | Corrigido na configuração do Spring Security com `Cross-Origin-Resource-Policy: same-origin`. A opção é compatível com a API e a documentação servidas no mesmo host. O scan deve ser executado novamente para validar a correção. |
| Respostas HTTP 4xx | Informativa | 38 ocorrências no scan sem autenticação e 28 no autenticado | Indica chamadas sem credenciais, dados ou identificadores válidos; não representa vulnerabilidade por si só. | Avaliado como comportamento esperado em endpoints protegidos e que exigem dados de negócio. |
| `Non-Storable Content` | Informativa | Presente nos dois scans | Indica respostas que não devem ser armazenadas em cache. | Sem ação: comportamento compatível com respostas dinâmicas da API. |

Os relatórios brutos produzidos pelo ZAP são `zap-api-report.html` e `zap-api-report-authenticated.html`. Eles devem ser gerados novamente quando houver mudança relevante na API ou na configuração de segurança.

## Resultados do SonarQube

O SonarQube deve ser executado conforme as instruções do README. Antes da entrega, registrar abaixo a data da execução, o *Quality Gate* obtido e os achados que demandaram correção.

| Data | Quality Gate | Bugs | Vulnerabilidades | Security Hotspots | Ação tomada |
| --- | --- | ---: | ---: | ---: | --- |
| A preencher na execução final | A preencher | A preencher | A preencher | A preencher | A preencher |

## Conclusão

Nos scans dinâmicos realizados, não foram identificados alertas de severidade alta ou média. O alerta de severidade baixa relacionado ao cabeçalho `Cross-Origin-Resource-Policy` foi corrigido com a política `same-origin`; a execução final do ZAP deve confirmar sua remoção. Os resultados do SonarQube devem ser consolidados nesta seção antes da entrega final.
