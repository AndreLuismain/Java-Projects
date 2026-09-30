# 10 - Agente Documentador de Codigo Legado

## Objetivo

API REST para receber arquivos de codigo-fonte, analisar sua logica com um agente de IA e gerar documentacao tecnica em Markdown, mantendo historico consultavel.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Validation e Data JPA
- PostgreSQL ou H2 para desenvolvimento
- Apache Tika ou validacao por extensao e tamanho
- Gemini API ou gateway LLM equivalente
- Flyway, JUnit 5, Mockito e Testcontainers
- Maven

## Requisitos e execucao

Necessarios JDK 21, Maven 3.9+ e `LLM_API_KEY`. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` para banco externo.

```bash
mvn clean verify
mvn spring-boot:run
```

## Arquitetura

O fluxo separa upload, armazenamento, extracao, analise e geracao do documento. O arquivo original deve ser tratado como artefato imutavel e o LLM deve ser acessado por uma interface substituivel.

Consulte [TECH_SPEC.md](TECH_SPEC.md).
