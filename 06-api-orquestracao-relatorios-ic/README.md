# 06 · API de Orquestração de Relatórios para Iniciação Científica

Microsserviço backend em **Java 21** e **Spring Boot 3** projetado para receber apontamentos semanais de atividades de Iniciação Científica, classificar automaticamente o esforço por categoria acadêmica via **Agente LLM** (com fallback heurístico determinístico) e consolidar relatórios formais em **Markdown** voltados ao professor orientador.

> 📄 Especificação técnica detalhada e contratos de dados disponíveis em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Idempotência de Submissão (`WeeklyLogService`):** Prevenção de duplicidade por combinação `(studentId, weekStartDate)` e hash SHA-256 do conteúdo.
* **Classificação de Esforço com Resiliência (`LlmClassificationGateway` & `HeuristicLlmGateway`):** Categorização automática das atividades (`LITERATURE_REVIEW`, `IMPLEMENTATION`, `EXPERIMENTATION`, `WRITING`, `MEETING`, `OTHER`) garantindo que a soma das horas alocadas nas categorias corresponda exatamente ao `totalHoursReported`.
* **Geração Estruturada de Relatórios (`MarkdownReportGenerator`):** Compilação determinística de documento Markdown com cabeçalho de metadados, tabela de distribuição de horas por categoria, síntese executiva, bloqueios e próximos passos.
* **Versionamento de Schema:** Migrações controladas via **Flyway** (`V1__init.sql`).

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/ic-reports/logs` | Registra um novo log semanal de atividades de IC com validação de horas e idempotência. |
| `POST` | `/api/v1/ic-reports/logs/{logId}/generate` | Orquestra a classificação via LLM e gera o relatório consolidado em Markdown. |
| `GET` | `/api/v1/ic-reports/logs/{logId}/report` | Retorna os metadados estruturados e o resumo do relatório gerado. |
| `GET` | `/api/v1/ic-reports/logs/{logId}/report/markdown` | Exporta o conteúdo puro (`text/markdown`) pronto para envio ao orientador. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 06-api-orquestracao-relatorios-ic
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
