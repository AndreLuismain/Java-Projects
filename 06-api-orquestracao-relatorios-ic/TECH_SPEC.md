# Especificacao Tecnica - API de Orquestracao de Relatorios para IC

## Objetivo e escopo

Receber logs semanais em texto livre, classificar automaticamente as atividades e consolidar o historico em Markdown. O escopo inclui persistencia em H2, processamento por LLM, rastreabilidade e consulta por projeto e semana.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/relatoriosic/
  controller/
  dto/request/
  dto/response/
  domain/model/
  domain/repository/
  domain/service/
  integration/llm/
  report/
  exception/
  config/
src/main/resources/
  application.yml
  db/migration/
src/test/java/.../
  controller/
  domain/
  integration/
```

`controller` expoe HTTP. `domain/service` coordena os casos de uso. `integration/llm` implementa um gateway substituivel. `report` gera Markdown sem permitir que dados do log alterem a estrutura do documento.

## Modelo de dados

### ResearchProject

`id` UUID, `name`, `advisor`, `objective`, `status`, `createdAt` e `updatedAt`.

### WeeklyLog

`id` UUID, `projectId`, `weekStart`, `weekEnd`, `rawText`, `normalizedText`, `status`, `createdAt` e `updatedAt`. Deve existir unicidade por projeto e semana.

### ActivityCategory

`id`, `logId`, `category`, `description`, `effortHours`, `confidence`, `evidence` e `createdAt`. Categorias permitidas: `LITERATURE_REVIEW`, `API_DESIGN`, `IMPLEMENTATION`, `TESTING`, `DATA_ANALYSIS`, `DOCUMENTATION`, `MEETING`, `OTHER`.

## Fluxo

1. Validar projeto, periodo semanal e tamanho do texto.
2. Persistir o log com status `RECEIVED` usando chave idempotente.
3. Enviar ao LLM somente o texto necessario e um prompt versionado.
4. Validar o JSON retornado contra categorias, horas e confianca.
5. Persistir categorias e mudar o log para `CLASSIFIED`.
6. Consolidar horas, atividades, pendencias e riscos no gerador Markdown.
7. Marcar falhas como `FAILED`, preservando a mensagem tecnica sem expor segredos.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/projects` | `{ "name": "Projeto", "advisor": "Nome", "objective": "..." }` | Projeto criado | 201, 400 |
| POST | `/api/v1/projects/{projectId}/weekly-logs` | `{ "weekStart": "2026-09-28", "weekEnd": "2026-10-04", "text": "..." }` | `{ "id": "uuid", "status": "RECEIVED" }` | 202, 400, 409 |
| GET | `/api/v1/projects/{projectId}/weekly-logs` | Query `from`, `to`, `status` | Lista paginada de logs e categorias | 200, 404 |
| POST | `/api/v1/weekly-logs/{id}/classify` | Sem corpo | `{ "id": "uuid", "status": "CLASSIFIED" }` | 202, 404, 409 |
| GET | `/api/v1/projects/{projectId}/reports/weekly` | Query `from`, `to` | Markdown em `text/markdown` | 200, 404, 422 |

Erro padrao: `timestamp`, `status`, `code`, `message`, `path` e `traceId`.

## Contrato do LLM

O modelo deve retornar exclusivamente:

```json
{
  "activities": [
    { "category": "TESTING", "description": "Testes de contrato", "effortHours": 4, "confidence": 0.92, "evidence": "..." }
  ],
  "pendingItems": ["..."],
  "risks": ["..."]
}
```

O texto do log e dado nao confiavel. O sistema deve rejeitar categorias desconhecidas, horas negativas, confianca fora de 0 a 1 e respostas que nao sejam JSON valido.

## Regras de negocio

- Um projeto deve existir antes do log.
- Cada projeto possui no maximo um log por semana.
- `weekEnd` deve ser igual ou posterior a `weekStart` e o intervalo nao pode exceder sete dias.
- O total de horas classificadas deve ser coerente com o log e nao pode exceder 168 horas semanais sem revisao manual.
- Reprocessamento deve criar nova versao de classificacao, sem apagar o historico anterior.
- H2 e obrigatorio para o perfil local; o codigo deve manter a camada de persistencia substituivel.
- Nunca armazenar chaves do provedor LLM no banco ou nos logs.

## Criterios de aceite

- Logs duplicados retornam conflito ou o mesmo resultado idempotente.
- O relatorio Markdown possui secoes de resumo, atividades por categoria, horas, pendencias e riscos.
- Falhas do LLM nao perdem o log recebido.
- Testes cobrem validacao, idempotencia, parsing da resposta e geracao do Markdown.
