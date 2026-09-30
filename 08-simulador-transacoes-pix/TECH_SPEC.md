# Especificacao Tecnica - Simulador de Transacoes Pix

## Objetivo

Simular transferencias entre contas virtuais com invariantes financeiros explicitos: saldo nunca negativo, valor transferido preservado e extrato imutavel.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/pix/
  controller/
  dto/request/
  dto/response/
  domain/model/
  domain/service/
  domain/event/
  repository/
  integration/event/
  exception/
  config/
src/main/resources/db/migration/
src/test/java/br/com/andreluismain/pix/
```

## Modelo de dados

`WalletAccount`: UUID, ownerId, pixKey, balanceInCents, currency, status, version, createdAt e updatedAt.

`Transfer`: UUID, idempotencyKey, sourceAccountId, destinationAccountId, amountInCents, status `COMPLETED`, `REJECTED` ou `FAILED`, correlationId e createdAt.

`LedgerEntry`: UUID, transferId, accountId, entryType `DEBIT` ou `CREDIT`, amountInCents, balanceAfterInCents, createdAt. Nunca atualizar ou excluir uma entrada.

`OutboxEvent`: UUID, aggregateId, eventType, payload, status, attempts, createdAt e publishedAt.

## Fluxo transacional

1. Validar chave de idempotencia, contas, moeda e valor positivo.
2. Bloquear as duas contas em ordem deterministica por UUID.
3. Revalidar status e saldo dentro da transacao.
4. Criar transferencia, debito, credito e atualizar saldos.
5. Criar evento de seguranca na outbox quando o valor exceder `PIX_SECURITY_LIMIT`.
6. Confirmar a transacao; um publicador posterior envia eventos pendentes.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/accounts` | `{ "ownerId": "uuid", "pixKey": "...", "currency": "BRL" }` | Conta criada sem dados sensiveis | 201, 400, 409 |
| GET | `/api/v1/accounts/{id}` | Parametro de rota | Saldo e dados publicos | 200, 404 |
| POST | `/api/v1/transfers` | Header `Idempotency-Key`; `{ "sourceAccountId": "uuid", "destinationAccountId": "uuid", "amountInCents": 1000 }` | Transferencia e status | 201, 400, 409, 422 |
| GET | `/api/v1/accounts/{id}/statement` | Query `from`, `to`, `page`, `size` | Extrato paginado | 200, 404 |
| GET | `/api/v1/transfers/{id}` | Parametro de rota | Detalhes da transferencia | 200, 404 |

## Regras de seguranca e negocio

- Valores monetarios usam inteiros em centavos; nunca usar `double`.
- A chave de idempotencia e obrigatoria, limitada e associada ao mesmo payload.
- Conta de origem e destino devem ser diferentes, ativas e ter a mesma moeda.
- O saldo nao pode ficar negativo.
- Falhas de concorrencia devem ser tratadas com retry limitado ou conflito explicito.
- O extrato nao pode ser editado por endpoint publico.
- Logs nao podem conter chaves Pix completas, credenciais ou dados desnecessarios.
- O evento acima do limite deve ser criado no mesmo commit da transferencia.

## Evento de seguranca

Tipo: `PIX_TRANSFER_ABOVE_SECURITY_LIMIT`. Payload minimo: `eventId`, `transferId`, `amountInCents`, `sourceAccountId`, `createdAt` e `reason`. O consumidor deve ser idempotente.

## Criterios de aceite

- Repetir a mesma chave de idempotencia retorna o resultado original sem novo debito.
- Transferencias concorrentes nunca permitem saldo negativo.
- A soma de debitos e creditos do ledger e zero por transferencia concluida.
- O evento acima do limite nao desaparece quando o publicador esta indisponivel.
- Testes cobrem concorrencia, rollback, duplicidade, precisao monetaria e imutabilidade.
