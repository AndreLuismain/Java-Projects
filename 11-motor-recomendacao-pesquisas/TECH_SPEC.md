# Especificacao Tecnica - Motor de Recomendacao de Pesquisas Academicas

## Objetivo

Receber o perfil de um estudante e um conjunto de vagas de pesquisa, executar uma pipeline ETL para normalizar os dados e calcular um ranking explicavel de compatibilidade.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/recomendacao/
  controller/
  dto/request/
  dto/response/
  domain/model/
  domain/service/
  etl/
    extract/
    transform/
    load/
  scoring/
  integration/llm/
  repository/
  exception/
  config/
src/test/java/br/com/andreluismain/recomendacao/
  etl/
  scoring/
  integration/
```

## Modelo de dados

`StudentProfile`: UUID, semester, technologies, researchAreas, interests, availabilityHours e createdAt.

`ResearchOpportunity`: UUID, sourceId, title, institution, description, normalizedTechnologies, normalizedAreas, modality, deadline, contentHash e createdAt.

`RecommendationRun`: UUID, profileId, status, pipelineVersion, modelName, createdAt e completedAt.

`Recommendation`: UUID, runId, opportunityId, deterministicScore, semanticScore, finalScore, reasons e rank.

## Pipeline ETL

1. Extract: validar o payload e converter aliases de campos para um modelo bruto comum.
2. Transform: normalizar caixa, acentos, tecnologias, areas, modalidade, datas e carga horaria.
3. Transform: remover duplicatas por URL canonica e hash de conteudo, registrando conflitos.
4. Load: persistir oportunidades normalizadas e versao da pipeline.
5. Score: calcular aderencia de semestre, tecnologias, areas, modalidade e prazo.
6. Enriquecer: opcionalmente solicitar score semantico ao LLM com explicacoes curtas.
7. Ordenar, atribuir rank e retornar JSON estavel.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/profiles` | `{ "semester": 5, "technologies": ["Java"], "researchAreas": ["IA"], "availabilityHours": 10 }` | Perfil criado | 201, 400 |
| POST | `/api/v1/recommendations` | `{ "profileId": "uuid", "opportunities": [{ "title": "...", "description": "...", "technologies": ["Java"] }], "useLlm": true }` | `{ "runId": "uuid", "status": "PROCESSING" }` | 202, 400 |
| GET | `/api/v1/recommendations/{runId}` | Query `page`, `size`, `minScore` | Ranking paginado | 200, 404, 409 |
| POST | `/api/v1/opportunities/import` | Lista de vagas em JSON | Resultado de normalizacao | 202, 400 |

Resposta de ranking:

```json
{
  "runId": "uuid",
  "items": [
    {
      "rank": 1,
      "opportunityId": "uuid",
      "title": "Pesquisa em IA",
      "finalScore": 87.5,
      "reasons": ["Tecnologia Java compativel", "Area de IA aderente"]
    }
  ]
}
```

## Regras de scoring

- Score final entre 0 e 100.
- O componente deterministico deve ser calculavel sem LLM e representar no minimo 60 por cento do resultado.
- O LLM nao pode inventar requisitos; quando nao houver evidencia, retornar `INSUFFICIENT_DATA`.
- Empates devem ser resolvidos por prazo mais proximo, depois titulo normalizado.
- O ranking deve registrar a versao da pipeline e do prompt.
- Oportunidades vencidas podem ser filtradas ou sinalizadas, conforme parametro explicito.

## Regras de negocio e seguranca

- Limitar quantidade de vagas e tamanho dos campos por requisicao.
- Deduplicar antes de chamar o LLM para reduzir custo.
- Validar datas e tecnologias contra listas canonicas versionadas.
- Nao enviar dados pessoais desnecessarios ao provedor externo.
- Cachear resultados somente com chave composta por perfil, oportunidades e versoes.
- Processar chamadas ao LLM com timeout, retry controlado e resposta JSON validada.

## Criterios de aceite

- Entradas com aliases equivalentes produzem o mesmo modelo normalizado.
- O ranking e ordenado por `finalScore` decrescente e possui justificativas.
- A falha do LLM nao impede o ranking deterministico, mas e registrada.
- Duplicatas nao aparecem duas vezes no resultado.
- Testes cobrem ETL, datas, deduplicacao, score, ordenacao e contrato JSON.
