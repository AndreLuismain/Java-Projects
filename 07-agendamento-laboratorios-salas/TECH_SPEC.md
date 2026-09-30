# Especificacao Tecnica - Agendamento de Laboratorios e Salas

## Objetivo

Gerenciar reservas de recursos compartilhados e impedir duas reservas simultaneas para o mesmo recurso e intervalo de tempo, inclusive sob concorrencia entre varias instancias da API.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/agendamento/
  controller/
  dto/request/
  dto/response/
  domain/model/
  domain/service/
  repository/
  exception/
  config/
  report/
src/main/resources/db/migration/
src/test/java/br/com/andreluismain/agendamento/
```

## Dados

`Resource`: UUID, nome, tipo `LABORATORY` ou `STUDY_ROOM`, capacidade e ativo.

`Reservation`: UUID, resourceId, userId, startAt, endAt, purpose, status `ACTIVE` ou `CANCELLED`, createdAt e cancelledAt.

Criar indice por `resource_id`, `start_at`, `end_at` e status. A estrategia de concorrencia deve ser documentada na implementacao e coberta por teste de integracao real.

## Regra de conflito

Duas reservas ativas conflitam quando:

```text
newStart < existingEnd AND newEnd > existingStart
```

A criacao deve ocorrer em uma transacao. Usar bloqueio pessimista na linha do recurso, isolamento adequado ou constraint nativa do banco. Uma verificacao apenas em memoria nao e aceita.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/resources` | `{ "name": "Lab 01", "type": "LABORATORY", "capacity": 20 }` | Recurso criado | 201, 400 |
| GET | `/api/v1/resources` | Query `type`, `availableFrom`, `availableTo` | Lista de recursos | 200 |
| POST | `/api/v1/reservations` | `{ "resourceId": "uuid", "userId": "uuid", "startAt": "...", "endAt": "...", "purpose": "..." }` | Reserva criada | 201, 400, 409 |
| DELETE | `/api/v1/reservations/{id}` | Sem corpo | 204 | 204, 404, 409 |
| GET | `/api/v1/reservations` | Query `resourceId`, `userId`, `from`, `to`, `status` | Pagina de reservas | 200 |
| GET | `/api/v1/reports/usage/weekly` | Query `weekStart`, `resourceId` | CSV `text/csv` | 200, 400 |

Erro padrao: `timestamp`, `status`, `code`, `message`, `path` e `traceId`.

## Relatorio CSV

Colunas obrigatorias: `resourceId`, `resourceName`, `date`, `reservedMinutes`, `reservationCount`, `utilizationPercent`.

O relatorio deve usar UTC ou timezone configurado explicitamente, ordenar por recurso e data e escapar campos conforme RFC 4180.

## Regras de negocio

- `startAt` deve ser anterior a `endAt`.
- A duracao maxima e 12 horas e reservas nao podem iniciar no passado alem da tolerancia configurada.
- Somente recursos ativos podem ser reservados.
- Cancelamento e idempotente e nao remove o registro historico.
- Somente o solicitante ou operador autorizado pode cancelar.
- O fuso horario deve ser explicito e consistente.
- Payloads tem limite de tamanho e todos os UUIDs devem ser validados.

## Criterios de aceite

- Dez requisicoes concorrentes para o mesmo intervalo resultam em no maximo uma reserva ativa.
- Cancelamento libera o intervalo sem apagar auditoria.
- O CSV nao inclui reservas canceladas no uso ativo.
- Testes cobrem conflito, concorrencia, timezone, autorizacao e formato CSV.
