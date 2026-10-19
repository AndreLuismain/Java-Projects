# 11 · Motor de Recomendação de Pesquisas Acadêmicas (Pipeline ETL & Scoring Híbrido)

API REST em **Java 21** e **Spring Boot 3** que executa um pipeline **ETL (Extração, Transformação, Normalização e Deduplicação)** sobre oportunidades de Iniciação Científica e calcula um **ranking explicável de compatibilidade** para perfis de estudantes, combinando **scoring determinístico multi-critério** e **scoring semântico via LLM**.

> Especificação técnica detalhada disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Pipeline ETL com Deduplicação (`OpportunityExtractor`, `OpportunityTransformer`, `OpportunityDeduplicator`, `OpportunityLoader`):**
  * Higienização textual, padronização de tags/requisitos em *lowercase*, validação de cargas horárias e geração de impressão digital (`fingerprint` SHA-256 sobre instituição + título normalizado + orientador) para eliminar vagas duplicadas entre múltiplas fontes.
* **Motor de Pontuação Híbrido e Explicável (`DeterministicScorer`, `LlmSemanticScorer`, `RecommendationRanker`):**
  * **Score Determinístico (70%):** Avalia interseção de áreas de interesse (40%), domínio de habilidades técnicas requeridas (35%), compatibilidade de carga horária semanal (15%) e aderência de modalidade presencial/remota/híbrida (10%).
  * **Score Semântico (30%):** Avalia afinidade contextual entre o resumo do estudante e a descrição da pesquisa, com fallback transparente para 100% determinístico caso o provedor LLM esteja indisponível (`fallbackUsed = true`).
  * **Desempate Determinístico:** Ordenação estrita por `finalScore DESC`, `deterministicScore DESC` e `opportunityId ASC`, acompanhada de justificativa auditável (`matchedSkills`, `missingSkills`, `explanation`).

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/etl/opportunities/ingest` | Executa o pipeline ETL para normalizar, deduplicar e persistir um lote de vagas de pesquisa. |
| `GET` | `/api/v1/opportunities` | Lista todas as oportunidades de pesquisa normalizadas e ativas. |
| `POST` | `/api/v1/profiles` | Cadastra o perfil acadêmico de um estudante (curso, semestre, skills, interesses, horas semanais). |
| `POST` | `/api/v1/recommendations/runs` | Dispara uma execução de ranqueamento para um estudante e persiste a trilha de recomendações. |
| `GET` | `/api/v1/recommendations/runs/{runId}` | Retorna o ranking ordenado de oportunidades com detalhamento de scores e justificativas. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 11-motor-recomendacao-pesquisas
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
