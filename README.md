# Portfólio de Engenharia Backend em Java (`Java-Projects`)

![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=flat-square&logo=hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_15+-316192?style=flat-square&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat-square&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![OpenAPI 3](https://img.shields.io/badge/OpenAPI_3-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![Gemini API](https://img.shields.io/badge/Gemini_API-4285F4?style=flat-square&logo=googlegemini&logoColor=white)

Repositório central de engenharia de software backend em Java desenvolvido por André Luís da Silva Souza (Sistemas de Informação — ICMC-USP). Reúne 11 projetos independentes que abrangem desde os fundamentos de Programação Orientada a Objetos (POO) e Programação Funcional com Streams no Java 21 até microsserviços transacionais, gateways reativos e sistemas de orquestração de Agentes de Inteligência Artificial.

---

## Catálogo e Índice de Navegação dos Projetos

| # | Projeto / Diretório | Stack / Tecnologias | Tipo / Arquitetura | Conceitos de Engenharia Aplicados | Especificação |
| :---: | :--- | :--- | :---: | :--- | :---: |
| **11** | **[`11-motor-recomendacao-pesquisas`](./11-motor-recomendacao-pesquisas/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white) | ![ETL & AI](https://img.shields.io/badge/ETL_%26_LLM-4285F4?style=flat-square) | Pipeline ETL (Extração, Normalização e Deduplicação SHA-256), Scoring Híbrido (70% Determinístico + 30% Semântico via LLM) e Fallback Transparente. | [`TECH_SPEC.md`](./11-motor-recomendacao-pesquisas/TECH_SPEC.md) |
| **10** | **[`10-agente-documentador-legado`](./10-agente-documentador-legado/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white) | ![Security & AI](https://img.shields.io/badge/AppSec_%26_AI-0F766E?style=flat-square) | Segurança em Uploads (Prevenção de *Path Traversal*), Sanitização Regex de Credenciais (`[REDACTED_SECRET]`), Cache Idempotente por Hash SHA-256 e Geração de Markdown. | [`TECH_SPEC.md`](./10-agente-documentador-legado/TECH_SPEC.md) |
| **09** | **[`09-api-gateway-agentes`](./09-api-gateway-agentes/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![WebFlux](https://img.shields.io/badge/WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white) ![Reactor](https://img.shields.io/badge/Reactor-2C2D72?style=flat-square) | ![Reactive Gateway](https://img.shields.io/badge/Reactive_API-7C3AED?style=flat-square) | Arquitetura Reativa Não-Bloqueante, Rate Limiting Thread-Safe (*Token Bucket* com `429` e `Retry-After`), Classificador de Complexidade Textual e Propagação de `X-Correlation-Id`. | [`TECH_SPEC.md`](./09-api-gateway-agentes/TECH_SPEC.md) |
| **08** | **[`08-simulador-transacoes-pix`](./08-simulador-transacoes-pix/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white) | ![Transactional Core](https://img.shields.io/badge/ACID_Ledger-1D4ED8?style=flat-square) | Precisão Monetária em Centavos (`Long`), Prevenção Determinística de Deadlocks (`SELECT FOR UPDATE` ordenado por UUID), *Append-Only Ledger* (Partidas Dobradas), Idempotência Estrita e *Transactional Outbox*. | [`TECH_SPEC.md`](./08-simulador-transacoes-pix/TECH_SPEC.md) |
| **07** | **[`07-agendamento-laboratorios-salas`](./07-agendamento-laboratorios-salas/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![JPA](https://img.shields.io/badge/JPA_Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white) | ![Concurrency Control](https://img.shields.io/badge/Pessimistic_Lock-B45309?style=flat-square) | Controle de Concorrência Pessimista (`PESSIMISTIC_WRITE`) contra *Double-Booking*, Cálculo de Interseção de Intervalos Temporais e Exportação Analítica em CSV. | [`TECH_SPEC.md`](./07-agendamento-laboratorios-salas/TECH_SPEC.md) |
| **06** | **[`06-api-orquestracao-relatorios-ic`](./06-api-orquestracao-relatorios-ic/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white) | ![REST & AI](https://img.shields.io/badge/REST_%26_Agent-4285F4?style=flat-square) | Ingestão Idempotente de Logs Semanais de IC, Classificação de Esforço por Categoria via Agente LLM com Fallback Heurístico e Compilação de Relatórios Markdown. | [`TECH_SPEC.md`](./06-api-orquestracao-relatorios-ic/TECH_SPEC.md) |
| **05** | **[`analisador-editais-ic`](./analisador-editais-ic/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white) | ![Async & Scraping](https://img.shields.io/badge/Anti--SSRF_%26_AI-0F766E?style=flat-square) | Web Scraping Seguro (Defesa Anti-SSRF contra IPs privados/loopback e Rate Limiting por domínio), Processamento Assíncrono (`@Async`) e Integração com Google Gemini API. | [`TECH_SPEC.md`](./analisador-editais-ic/TECH_SPEC.md) |
| **04** | **[`04 API Gerenciamento Obras`](./04%20API%20Gerenciamento%20Obras/)** | ![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) | ![REST API](https://img.shields.io/badge/Layered_REST-2563EB?style=flat-square) | Arquitetura em Camadas, DTOs com Java Records, `@RestControllerAdvice` Global e Agregação Financeira no SGBD via Projeções JPA (`CustoPorDistribuidoraProjection`). | [README Local](./04%20API%20Gerenciamento%20Obras/README.md) |
| **03** | **[`03 Analisador Dados Aneel`](./03%20Analisador%20Dados%20Aneel/)** | ![Java 21](https://img.shields.io/badge/Java_21_SE-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Streams API](https://img.shields.io/badge/Streams_API-0284C7?style=flat-square) | ![Functional Core](https://img.shields.io/badge/CLI_Analytics-475569?style=flat-square) | Parser CSV Tolerante a Aspas, *Records*, *Sealed Interfaces*, *Pattern Matching for `switch`*, *Generics* com `Function<String, Optional<T>>`, `Stream API` e `TreeMap`. | [README Local](./03%20Analisador%20Dados%20Aneel/README.md) |
| **02** | **[`02 API de gerenciamento RPG`](./02%20API%20de%20gerenciamento%20RPG/)** | ![Java 17](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white) | ![REST CRUD](https://img.shields.io/badge/REST_CRUD-2563EB?style=flat-square) | API REST CRUD completa, Isolamento de Entidades com DTOs, Documentação Swagger/OpenAPI e Conteinerização com Dockerfile. | [README Local](./02%20API%20de%20gerenciamento%20RPG/README.md) |
| **01** | **[`01 Mini RPG`](./01%20Mini%20RPG/)** | ![Java SE](https://img.shields.io/badge/Java_SE-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![OOP](https://img.shields.io/badge/Core_OOP-334155?style=flat-square) | ![OOP Engine](https://img.shields.io/badge/Console_OOP-475569?style=flat-square) | Fundamentos de Programação Orientada a Objetos (POO): Herança (`extends`), Polimorfismo (`@Override`), Encapsulamento (`protected`/`private`) e Membros Estáticos (`static`). | [README Local](./01%20Mini%20RPG/README.md) |

---

## Padrões Arquiteturais e Conceitos Aplicados

1. **Consistência Transacional e Concorrência no Banco de Dados:**
   - Bloqueio pessimista (`LockModeType.PESSIMISTIC_WRITE`) com ordenação lexicográfica determinística de chaves para eliminar *deadlocks* em transferências simultâneas ([Projeto 08](./08-simulador-transacoes-pix/)) e impedir reservas conflitantes de recursos ([Projeto 07](./07-agendamento-laboratorios-salas/)).
   - Livro-razão contábil imutável (*Append-Only Ledger*) com invariante de soma zero e *Transactional Outbox* para publicação confiável de alertas antifraude ([Projeto 08](./08-simulador-transacoes-pix/)).

2. **Sistemas Reativos, Resiliência e Rate Limiting:**
   - Pipeline reativo não-bloqueante com Spring WebFlux e Project Reactor, controle de taxa em memória via *Token Bucket* com cabeçalho `Retry-After` e rastreamento distribuído por `X-Correlation-Id` ([Projeto 09](./09-api-gateway-agentes/)).

3. **Engenharia de Agentes de IA e Pipelines ETL Seguros:**
   - Defesa contra SSRF (*Server-Side Request Forgery*) na coleta de páginas externas ([Projeto 05](./analisador-editais-ic/)) e contra *Path Traversal* e exposição de credenciais em códigos-fonte legados ([Projeto 10](./10-agente-documentador-legado/)).
   - Esteira ETL com normalização, deduplicação por impressão digital SHA-256 e ranqueamento híbrido combinando regras determinísticas e avaliação semântica via LLM ([Projeto 11](./11-motor-recomendacao-pesquisas/)).

---

## Matriz Tecnológica

| Categoria | Tecnologias e Ferramentas | Especificação Técnica |
| :--- | :--- | :--- |
| **Linguagens** | ![Java 21](https://img.shields.io/badge/Java_21_LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Java 17](https://img.shields.io/badge/Java_17_LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![SQL](https://img.shields.io/badge/SQL-336791?style=flat-square&logo=postgresql&logoColor=white) | Uso de Records, Sealed Interfaces, Pattern Matching, Streams API e Concorrência. |
| **Ecossistema Spring** | ![Spring Boot 3](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white) ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=flat-square&logo=hibernate&logoColor=white) | APIs RESTful bloqueantes (Web MVC) e reativas (WebFlux), Hibernate 6 e Jakarta Validation. |
| **Persistência e Migrações** | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL_15+-316192?style=flat-square&logo=postgresql&logoColor=white) ![H2](https://img.shields.io/badge/H2_Database-004088?style=flat-square) ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white) | Modelagem relacional, controle de concorrência pessimista e versionamento de schema SQL. |
| **Testes e Qualidade** | ![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat-square&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-78A641?style=flat-square&logo=openjdk&logoColor=white) ![AssertJ](https://img.shields.io/badge/AssertJ-25A162?style=flat-square) | Testes unitários isolados, MockMvc, WebTestClient e MockWebServer para clientes HTTP. |
| **Build e Infraestrutura** | ![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) ![OpenAPI](https://img.shields.io/badge/Swagger_OpenAPI-85EA2D?style=flat-square&logo=swagger&logoColor=black) | Gerenciamento de dependências via Maven, conteinerização e documentação interativa. |

---

## Instruções de Compilação e Execução

Cada subpasta constitui um projeto isolado e autocontido.

### Microsserviços e APIs Spring Boot (Projetos 02, 04, 05, 06, 07, 08, 09, 10 e 11)
Acesse o diretório do projeto desejado e utilize o Maven (ou o wrapper `./mvnw`):
```powershell
cd 08-simulador-transacoes-pix
mvn clean test
mvn spring-boot:run
```
A aplicação iniciará por padrão em `http://localhost:8080` (com documentação OpenAPI em `http://localhost:8080/swagger-ui.html` nos projetos equipados com Springdoc OpenAPI).

### Aplicações Console Core Java (Projetos 01 e 03)
Compile e execute diretamente com `javac` e `java` (JDK 21):
```powershell
cd "03 Analisador Dados Aneel/src"
javac *.java
java central
```
