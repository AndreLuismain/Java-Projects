# 11 - Motor de Recomendacao de Pesquisas Academicas

## Objetivo

API de recomendacao para Iniciacao Cientifica que normaliza vagas, compara cada oportunidade com o perfil do estudante e retorna um ranking de compatibilidade.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Validation e WebFlux
- Jackson e Spring Data JPA
- PostgreSQL ou H2 para testes
- Gemini API ou outro gateway LLM
- JUnit 5, Mockito, WireMock e Testcontainers
- Maven

## Requisitos e execucao

Necessarios JDK 21, Maven 3.9+ e `LLM_API_KEY` quando a etapa semantica estiver habilitada.

```bash
mvn clean verify
mvn spring-boot:run
```

## Arquitetura

A pipeline ETL recebe dados heterogeneos, normaliza campos e deduplica oportunidades antes da etapa de scoring. O ranking combina regras deterministicas com score semantico validado pelo gateway LLM.

Consulte [TECH_SPEC.md](TECH_SPEC.md).
