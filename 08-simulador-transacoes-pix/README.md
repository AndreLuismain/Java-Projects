# 08 - Simulador de Transacoes Pix

## Objetivo

API para simular transferencias Pix entre contas de uma carteira virtual, mantendo saldo consistente, extrato imutavel e eventos de seguranca para transferencias acima do limite configurado.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Data JPA e Validation
- PostgreSQL ou H2 para testes locais
- Flyway
- Outbox ou mensageria para eventos
- JUnit 5, Testcontainers e Mockito
- Maven

## Execucao

Configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` e `PIX_SECURITY_LIMIT`.

```bash
mvn clean verify
mvn spring-boot:run
```

## Arquitetura

O caso de uso de transferencia e transacional. O ledger e append-only; o saldo e atualizado com bloqueio de conta e o evento de seguranca usa o padrao transactional outbox para nao depender de envio externo dentro da transacao.

Consulte [TECH_SPEC.md](TECH_SPEC.md).
