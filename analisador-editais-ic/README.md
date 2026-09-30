# Agente Analisador de Editais de IC

## Visao geral

API REST em Java para coletar editais e oportunidades academicas publicadas na web, extrair seu conteudo, gerar resumos com Gemini Pro e classificar a aderencia ao perfil de um estudante ou pesquisador.

A especificacao completa de arquitetura, dados, endpoints, regras de seguranca e criterios de aceite esta em [TECH_SPEC.md](TECH_SPEC.md).

## Tecnologias

- Java 21
- Spring Boot
- Spring WebFlux e Spring Data JPA
- PostgreSQL
- Flyway
- Jsoup
- Jackson
- Gemini API
- Maven
- JUnit 5, Mockito, WireMock e Testcontainers

## Requisitos de ambiente

- JDK 21 ou superior compativel.
- Maven 3.9 ou superior.
- Docker e Docker Compose para o PostgreSQL local.
- Chave de acesso da Gemini API.

Configure as variaveis de ambiente antes da execucao:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/editais_ic
DATABASE_USERNAME=editais
DATABASE_PASSWORD=editais
GEMINI_API_KEY=sua-chave-local
GEMINI_MODEL=gemini-pro
```

Nunca versione chaves, senhas ou arquivos `.env`.

## Build e execucao

Quando a implementacao Maven estiver criada:

```bash
mvn clean verify
mvn spring-boot:run
```

Para iniciar somente a dependencia de banco, o projeto devera fornecer um `docker-compose.yml` com PostgreSQL. A API sera disponibilizada em `http://localhost:8080`.

## Arquitetura

O sistema segue uma arquitetura em camadas com controllers REST, casos de uso no dominio, repositorios, adapters de scraping e um gateway isolado para o Gemini. A comunicacao externa deve ser assincrona, com timeout, limites de tamanho, retry controlado e validacao rigorosa do JSON retornado pelo modelo.

O contrato detalhado dos endpoints e o fluxo de processamento estao definidos em [TECH_SPEC.md](TECH_SPEC.md).
