# 05 · Agente Analisador de Editais de Iniciação Científica

API REST em **Java 21** e **Spring Boot 3** para coleta automatizada de editais acadêmicos públicos na web, extração higienizada de conteúdo HTML com **Jsoup** (com proteção anti-SSRF e Rate Limiting por domínio) e análise semântica assíncrona via **Google Gemini API** para classificar a aderência de oportunidades ao perfil de estudantes e pesquisadores.

> A especificação técnica completa de arquitetura, modelo de dados, contratos REST e critérios de aceite está disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Problema Resolvido e Destaques de Engenharia

* **Proteção Anti-SSRF (`SsrfValidator`):** Validação rigorosa de URLs antes da coleta, bloqueando esquemas não-HTTP/HTTPS, *loopback* (`127.0.0.1`, `localhost`, `::1`), redes privadas RFC 1918 (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`) e *link-local* (`169.254.0.0/16`).
* **Extração Limpa e Rate Limiting (`HtmlExtractor` & `RateLimiterService`):** Remoção de tags de ruído (`script`, `style`, `nav`, `footer`, `aside`), normalização textual e controle de frequência de requisições por domínio.
* **Idempotência por Hash SHA-256:** Cálculo de hash do conteúdo extraído para evitar reprocessamento redundante de editais inalterados.
* **Orquestração Assíncrona e Resiliente (`AnalysisService` & `GeminiGatewayImpl`):** Processamento em background via pool dedicado (`@Async`), cliente reativo `WebClient` com timeout estrito, *exponential backoff retry* e validação estrutural do JSON retornado pelo LLM (`score` 0–100, `classification`, `summary`, `requirements`).

---

## 2. Stack Tecnológica

* **Java 21 LTS** & **Spring Boot 3.2.5**
* **Spring Data JPA**, **Hibernate** & **Flyway** (`V1__init.sql`)
* **PostgreSQL** (produção/Docker) & **H2 Database** (testes automatizados)
* **Spring WebFlux (`WebClient`)** para comunicação não-bloqueante com a Gemini API
* **Jsoup 1.17.2** para web scraping e sanitização de HTML
* **Springdoc OpenAPI 3 (Swagger UI)** para documentação interativa
* **JUnit 5, Mockito e MockWebServer** para testes unitários e de integração

---

## 3. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/profiles` | Cadastra o perfil acadêmico de um estudante/pesquisador (interesses, skills, disponibilidade). |
| `GET` | `/api/v1/profiles/{id}` | Consulta os dados de um perfil cadastrado. |
| `POST` | `/api/v1/opportunities/collect` | Coleta síncrona de uma URL de edital com validação anti-SSRF, extração HTML e deduplicação SHA-256. |
| `POST` | `/api/v1/opportunities/search` | Dispara um job assíncrono (`202 Accepted`) para coleta em lote de múltiplas URLs. |
| `GET` | `/api/v1/opportunities` | Lista oportunidades coletadas com paginação e filtros (`status`, `classification`, `minScore`, `profileId`). |
| `GET` | `/api/v1/opportunities/{id}` | Retorna os detalhes completos do edital e a última análise gerada. |
| `POST` | `/api/v1/opportunities/{id}/analyze` | Inicia a análise assíncrona (`202 Accepted`) de compatibilidade entre o edital e um `profileId` via Gemini. |
| `GET` | `/api/v1/jobs/{jobId}` | Consulta o estado de execução (`PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`) de um job assíncrono. |

---

## 4. Como Compilar, Testar e Executar

### Variáveis de Ambiente
```text
DATABASE_URL=jdbc:postgresql://localhost:5432/editais_ic
DATABASE_USERNAME=editais
DATABASE_PASSWORD=editais
GEMINI_API_KEY=sua-chave-gemini
GEMINI_MODEL=gemini-pro
```

### Executar Testes Automatizados
```powershell
cd analisador-editais-ic
mvn clean test
```

### Subir Banco PostgreSQL e Executar a API
```powershell
cd analisador-editais-ic
docker-compose up -d
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
