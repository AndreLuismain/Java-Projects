# 08 · Simulador de Transações Pix (Concorrência, Ledger Imutável & Outbox)

API financeira em **Java 21** e **Spring Boot 3** que simula transferências instantâneas Pix entre carteiras digitais com **consistência transacional ACID**, **prevenção determinística de deadlocks**, **livro-razão contábil imutável (*Append-Only Ledger*)**, **idempotência estrita** e **Transactional Outbox** para eventos antifraude.

> 📄 Especificação técnica detalhada disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Precisão Monetária Absoluta:** Todos os saldos e valores de transferência são modelados estritamente em **centavos inteiros (`Long`)**, eliminando erros de ponto flutuante (`double`/`float`).
* **Bloqueio Pessimista *Deadlock-Free* (`PixTransferService`):** Em transferências concorrentes cruzadas (Conta A → Conta B e Conta B → Conta A simultaneamente), os UUIDs das contas são ordenados lexicograficamente antes da aquisição do lock pessimista (`SELECT ... FOR UPDATE`), garantindo ordem global de bloqueio e zero deadlocks.
* **Append-Only Ledger (Partidas Dobradas):** Cada transferência concluída registra dois lançamentos imutáveis (`DEBIT` com valor negativo `-amountCents` e `CREDIT` com valor positivo `+amountCents`), mantendo a invariante contábil onde a soma líquida por transação é exatamente `0`.
* **Idempotência com Detecção de Conflito (`Idempotency-Key`):** Requisições repetidas com a mesma chave e mesmo payload (hash SHA-256) retornam a resposta original sem duplicar débitos; se a mesma chave for reutilizada com payload divergente, retorna `422 Unprocessable Entity`.
* **Transactional Outbox (`OutboxEvent` & `OutboxPublisherScheduler`):** Transferências acima do limite de segurança (`PIX_SECURITY_LIMIT_CENTS`) gravam atomicamente um evento `HIGH_VALUE_TRANSFER_ALERT` na tabela `outbox_events` na mesma transação do débito/crédito, despachado assincronamente por worker agendado.

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/accounts` | Cria uma nova conta/carteira digital com saldo inicial em centavos. |
| `GET` | `/api/v1/accounts/{id}` | Consulta dados e saldo atual da conta. |
| `POST` | `/api/v1/pix/transfers` | Executa transferência Pix atômica (requer header `Idempotency-Key`). |
| `GET` | `/api/v1/accounts/{id}/statement` | Retorna o extrato imutável (*ledger*) e o histórico de transações da conta. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 08-simulador-transacoes-pix
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
