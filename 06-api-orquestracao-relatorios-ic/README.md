# 06 - API de Orquestracao de Relatorios para IC

## Objetivo

Microsservico Java para receber logs semanais de Iniciacao Cientifica, classificar o esforco com um agente LLM e gerar um relatorio Markdown estruturado para o professor orientador.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Validation e Data JPA
- H2 para persistencia relacional
- Jackson e cliente HTTP para o LLM
- JUnit 5, Mockito e Spring Boot Test
- Maven

## Requisitos

- JDK 21
- Maven 3.9 ou superior
- Chave da API do provedor LLM configurada em `LLM_API_KEY`

## Execucao

```bash
mvn clean verify
mvn spring-boot:run
```

A API sera disponibilizada em `http://localhost:8080`. O console H2 deve ficar habilitado somente em ambiente local.

## Arquitetura

A aplicacao separa controllers REST, casos de uso, repositorios JPA, gateway LLM e gerador de Markdown. O processamento da categorizacao deve ser assincrono, idempotente e auditavel.

Consulte [TECH_SPEC.md](TECH_SPEC.md) para o contrato completo.
