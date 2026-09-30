# 07 - Agendamento de Laboratorios e Salas

## Objetivo

Microsservico para consultar disponibilidade, criar e cancelar reservas de laboratorios e salas de estudo com controle rigoroso de concorrencia no banco.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Validation e Data JPA
- PostgreSQL ou H2 para testes
- Flyway
- JUnit 5, Mockito, Testcontainers e MockMvc
- Maven

## Requisitos e execucao

Necessarios JDK 21 e Maven 3.9+. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` quando usar PostgreSQL.

```bash
mvn clean verify
mvn spring-boot:run
```

## Arquitetura

A camada de dominio valida intervalos e regras de reserva; a camada de persistencia aplica transacao, indice de sobreposicao e bloqueio pessimista ou restricao equivalente. O relatorio semanal e produzido por uma consulta agregada e exportado como CSV.

Consulte [TECH_SPEC.md](TECH_SPEC.md) para os contratos.
