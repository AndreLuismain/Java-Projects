# Portfólio de Engenharia Backend em Java (`Java-Projects`)

Repositório central de engenharia de software backend em Java desenvolvido por André Luís da Silva Souza (Sistemas de Informação — ICMC-USP). Reúne 11 projetos independentes que abrangem desde os fundamentos de Programação Orientada a Objetos (POO) e Programação Funcional com Streams no Java 21 até microsserviços transacionais, gateways reativos e sistemas de orquestração de Agentes de Inteligência Artificial.

---

## Catálogo e Índice de Navegação dos Projetos

| # | Projeto / Diretório | Stack Principal | Conceitos de Engenharia Aplicados | Especificação |
| :---: | :--- | :--- | :--- | :---: |
| **11** | **[`11-motor-recomendacao-pesquisas`](./11-motor-recomendacao-pesquisas/)** | Java 21, Spring Boot 3, JPA, H2/PostgreSQL | Pipeline ETL (Extração, Normalização e Deduplicação SHA-256), Scoring Híbrido (70% Determinístico + 30% Semântico via LLM) e Fallback Transparente. | [`TECH_SPEC.md`](./11-motor-recomendacao-pesquisas/TECH_SPEC.md) |
| **10** | **[`10-agente-documentador-legado`](./10-agente-documentador-legado/)** | Java 21, Spring Boot 3, JPA, Flyway | Segurança em Uploads (Prevenção de *Path Traversal*), Sanitização Regex de Credenciais (`[REDACTED_SECRET]`), Cache Idempotente por Hash SHA-256 e Geração de Markdown. | [`TECH_SPEC.md`](./10-agente-documentador-legado/TECH_SPEC.md) |
| **09** | **[`09-api-gateway-agentes`](./09-api-gateway-agentes/)** | Java 21, Spring WebFlux, Project Reactor | Arquitetura Reativa Não-Bloqueante, Rate Limiting Thread-Safe (*Token Bucket* com `429` e `Retry-After`), Classificador de Complexidade Textual e Propagação de `X-Correlation-Id`. | [`TECH_SPEC.md`](./09-api-gateway-agentes/TECH_SPEC.md) |
| **08** | **[`08-simulador-transacoes-pix`](./08-simulador-transacoes-pix/)** | Java 21, Spring Boot 3, JPA, Flyway | Precisão Monetária em Centavos (`Long`), Prevenção Determinística de Deadlocks (`SELECT FOR UPDATE` ordenado por UUID), *Append-Only Ledger* (Partidas Dobradas), Idempotência Estrita e *Transactional Outbox*. | [`TECH_SPEC.md`](./08-simulador-transacoes-pix/TECH_SPEC.md) |
| **07** | **[`07-agendamento-laboratorios-salas`](./07-agendamento-laboratorios-salas/)** | Java 21, Spring Boot 3, JPA, Flyway | Controle de Concorrência Pessimista (`PESSIMISTIC_WRITE`) contra *Double-Booking*, Cálculo de Interseção de Intervalos Temporais e Exportação Analítica em CSV. | [`TECH_SPEC.md`](./07-agendamento-laboratorios-salas/TECH_SPEC.md) |
| **06** | **[`06-api-orquestracao-relatorios-ic`](./06-api-orquestracao-relatorios-ic/)** | Java 21, Spring Boot 3, JPA, Flyway | Ingestão Idempotente de Logs Semanais de IC, Classificação de Esforço por Categoria via Agente LLM com Fallback Heurístico e Compilação de Relatórios Markdown. | [`TECH_SPEC.md`](./06-api-orquestracao-relatorios-ic/TECH_SPEC.md) |
| **05** | **[`analisador-editais-ic`](./analisador-editais-ic/)** | Java 21, Spring Boot 3, WebFlux, Jsoup, PostgreSQL | Web Scraping Seguro (Defesa Anti-SSRF contra IPs privados/loopback e Rate Limiting por domínio), Processamento Assíncrono (`@Async`) e Integração com Google Gemini API. | [`TECH_SPEC.md`](./analisador-editais-ic/TECH_SPEC.md) |
| **04** | **[`04 API Gerenciamento Obras`](./04%20API%20Gerenciamento%20Obras/)** | Java 21, Spring Boot 3, PostgreSQL, Flyway, Docker | Arquitetura em Camadas, DTOs com Java Records, `@RestControllerAdvice` Global e Agregação Financeira no SGBD via Projeções JPA (`CustoPorDistribuidoraProjection`). | [README Local](./04%20API%20Gerenciamento%20Obras/README.md) |
| **03** | **[`03 Analisador Dados Aneel`](./03%20Analisador%20Dados%20Aneel/)** | Java 21 Standard Edition | Parser CSV Tolerante a Aspas, *Records*, *Sealed Interfaces*, *Pattern Matching for `switch`*, *Generics* com `Function<String, Optional<T>>`, `Stream API` e `TreeMap`. | [README Local](./03%20Analisador%20Dados%20Aneel/README.md) |
| **02** | **[`02 API de gerenciamento RPG`](./02%20API%20de%20gerenciamento%20RPG/)** | Java 17, Spring Boot 3.3.4, PostgreSQL, Docker | API REST CRUD completa, Isolamento de Entidades com DTOs, Documentação Swagger/OpenAPI e Conteinerização com Dockerfile. | [README Local](./02%20API%20de%20gerenciamento%20RPG/README.md) |
| **01** | **[`01 Mini RPG`](./01%20Mini%20RPG/)** | Java Standard Edition | Fundamentos de Programação Orientada a Objetos (POO): Herança (`extends`), Polimorfismo (`@Override`), Encapsulamento (`protected`/`private`) e Membros Estáticos (`static`). | [README Local](./01%20Mini%20RPG/README.md) |

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

## Tecnologias Utilizadas

- **Linguagens:** Java 21 LTS, Java 17 LTS, SQL
- **Ecossistema Spring:** Spring Boot 3.x, Spring Web MVC, Spring WebFlux (Project Reactor), Spring Data JPA (Hibernate 6), Jakarta Bean Validation
- **Bancos de Dados e Migrações:** PostgreSQL 15+, H2 Database, Flyway Migration
- **Documentação e Integração:** Springdoc OpenAPI 3 (Swagger UI), Jsoup, Jackson, Google Gemini API
- **Testes e Qualidade:** JUnit 5, Mockito, AssertJ, MockMvc, WebTestClient, MockWebServer
- **Infraestrutura e Build:** Apache Maven, Docker, Docker Compose

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
