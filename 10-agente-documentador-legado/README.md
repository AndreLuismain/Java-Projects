# 10 · Agente Documentador de Código Legado

API REST em **Java 21** e **Spring Boot 3** projetada para receber códigos-fonte legados (C, COBOL, Pascal, Java, Python, SQL), aplicar **defesa em profundidade na ingestão de arquivos** (proteção contra *Path Traversal*, validação de extensões e sanitização de segredos) e gerar documentação técnica estruturada em **Markdown** com cache idempotente por hash SHA-256.

> Especificação técnica detalhada disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Segurança de Ingestão (`FileValidator`):**
  * Bloqueio estrito contra ataques de *Path Traversal* (`..`, `/`, `\`, bytes nulos).
  * Whitelist de extensões de código-fonte permitidas (`.java`, `.c`, `.cpp`, `.h`, `.py`, `.cob`, `.cbl`, `.pas`, `.sql`, `.js`, `.ts`) e rejeição imediata de binários/executáveis ou arquivos vazios/acima do limite de bytes.
* **Sanitização de Credenciais (`SecretSanitizer`):** Antes de enviar qualquer trecho de código ao motor de IA, expressões regulares mascaram chaves de API, senhas hardcoded, tokens Bearer e credenciais JDBC com `[REDACTED_SECRET]`.
* **Idempotência e Cache por Conteúdo (`SHA-256`):** Arquivos idênticos submetidos para a mesma versão de prompt (`promptVersion`) reutilizam a análise previamente persistida sem custo adicional de inferência, a menos que `forceReanalyze=true` seja informado.
* **Geração Padronizada de Documentação (`MarkdownRenderer`):** Renderiza documento técnico contendo Visão Geral, Fluxo de Execução, Entradas/Saídas, Regras de Negócio Identificadas, Riscos/Dívida Técnica e Recomendações de Refatoração.

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/legacy-docs/artifacts` | Realiza upload/ingestão segura de código-fonte legado, sanitiza segredos e gera a documentação técnica. |
| `GET` | `/api/v1/legacy-docs/artifacts` | Lista todos os artefatos de código submetidos e seus respectivos hashes SHA-256. |
| `GET` | `/api/v1/legacy-docs/artifacts/{id}/analyses` | Lista o histórico de análises versionadas de um artefato. |
| `GET` | `/api/v1/legacy-docs/analyses/{analysisId}` | Consulta os detalhes estruturados de uma análise específica. |
| `GET` | `/api/v1/legacy-docs/analyses/{analysisId}/markdown` | Exporta a documentação técnica gerada em formato `text/markdown`. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 10-agente-documentador-legado
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
