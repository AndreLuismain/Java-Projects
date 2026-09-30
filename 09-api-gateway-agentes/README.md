# 09 - API Gateway de Agentes

## Objetivo

Gateway Java que mede a complexidade do texto recebido e direciona requisicoes para um processamento rapido ou para um fluxo com IA robusta, aplicando rate limiting e validacao de payload.

## Tecnologias

- Java 21 e Spring Boot
- Spring WebFlux e WebClient
- Resilience4j para timeout e circuit breaker
- Bucket4j ou Redis para rate limiting distribuido
- Jackson e Bean Validation
- JUnit 5, WireMock e Testcontainers
- Maven

## Execucao

Configure `FAST_PROCESSOR_URL`, `ROBUST_PROCESSOR_URL`, `RATE_LIMIT_CAPACITY` e, se distribuido, `REDIS_URL`.

```bash
mvn clean verify
mvn spring-boot:run
```

## Arquitetura

O gateway valida o request, calcula complexidade, seleciona uma rota e propaga um correlation ID. Adapters de destino ficam isolados do controller. O rate limiting deve ocorrer antes do encaminhamento.

Consulte [TECH_SPEC.md](TECH_SPEC.md).
