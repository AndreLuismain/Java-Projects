# 09 · API Gateway Reativo de Agentes de IA (Spring WebFlux & Rate Limiting)

Gateway reativo não-bloqueante desenvolvido em **Java 21**, **Spring WebFlux** e **Project Reactor** que analisa em tempo real a complexidade de payloads textuais, aplica controle de taxa (**Token Bucket Rate Limiting**) por cliente e roteia dinamicamente cada requisição entre um processador rápido (`FAST_PATH`) e um agente de IA profundo (`ROBUST_AGENT`).

> Especificação técnica detalhada disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Stack 100% Reativa (`Mono<ResponseEntity<GatewayResponse>>`):** Pipeline assíncrono não-bloqueante construído sobre Project Reactor para alto throughput sob baixa utilização de threads.
* **Rate Limiting Thread-Safe (Algoritmo Token Bucket — `TokenBucketRateLimiter`):** Controle de cota por `X-Client-Id` (ou IP) utilizando operações atômicas (`ConcurrentHashMap` + sincronização por bucket). Quando o limite é excedido, rejeita imediatamente com `429 Too Many Requests` e calcula o cabeçalho `Retry-After` em segundos.
* **Classificador Determinístico de Complexidade (`ComplexityAnalyzer`):** Calcula score normalizado `[0.0, 1.0]` combinando volume de caracteres, contagem de tokens, estrutura de múltiplas seções e densidade de vocabulário técnico/analítico para decidir entre `FAST_PATH` e `ROBUST_AGENT` (ou respeitar `routingHint` explícito).
* **Rastreabilidade Distribuída (`X-Correlation-Id`):** Geração ou propagação automática do identificador de correlação em todos os cabeçalhos HTTP, logs estruturados e respostas de erro.

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/gateway/process` | Analisa a complexidade do texto, valida rate limit (`X-Client-Id`), despacha ao agente adequado e retorna o resultado com métricas de latência e `X-Correlation-Id`. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 09-api-gateway-agentes
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
