# Meus Projetos em Java

Bem-vindo ao repositório de projetos em **Java**! 

Este espaço é dedicado a documentar minha evolução na linguagem e a construção de uma sólida base de engenharia de software para desenvolvimento **Backend**, **Sistemas Distribuídos**, **Microsserviços** e **Arquiteturas Integradas com Inteligência Artificial**.

---

## 🚀 Tópicos e Conceitos Abordados

Ao longo dos projetos, aplico padrões de design, boas práticas e conceitos avançados de engenharia:
- **Padrões Arquiteturais:** Domain-Driven Design (DDD), Transactional Outbox, Append-Only Ledger, API Gateway, ETL Pipelines.
- **Concorrência e Transacionalidade:** Bloqueio pessimista de escrita (`PESSIMISTIC_WRITE`), ordenação lexicográfica determinística anti-deadlock e controle otimista (`@Version`).
- **Resiliência e Reatividade:** Spring WebFlux, WebClient não-bloqueante, Rate Limiting com algoritmo Token Bucket e fallbacks heurísticos.
- **APIs RESTful e Validação:** Jakarta Bean Validation, contratos JSON padronizados com rastreabilidade via `X-Correlation-Id` e `traceId`.
- **Qualidade e Testes:** Testes unitários abrangentes com JUnit 5 e Mockito, cobrindo cenários felizes, borda, concorrência e idempotência.

---

## 📂 Projetos no Repositório

### 🎯 Microsserviços e Engenharia de Software (Projetos 05 a 11)

* **[11 - Motor de Recomendação de Pesquisas Acadêmicas](11-motor-recomendacao-pesquisas):**  
  API Java com pipeline ETL (Extract com aliases heterogêneos, Transform com normalização canônica, Deduplicate por hash SHA-256 e Load), além de motor híbrido de recomendação combinando pontuação determinística (mínimo 60% do peso) com enriquecimento semântico.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring Data JPA, H2/PostgreSQL.

* **[10 - Agente Documentador de Código Legado](10-agente-documentador-legado):**  
  API REST que recebe arquivos legados (COBOL, Pascal, C, etc.), valida integridade contra path traversal e extensões perigosas, sanitiza credenciais e utiliza agentes de IA / analisadores sintáticos para gerar documentação técnica completa em Markdown estruturado, com cache por hash SHA-256.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring Web, Data JPA, Gemini API / Heurística estruturada.

* **[09 - API Gateway de Agentes](09-api-gateway-agentes):**  
  Gateway reativo que avalia a complexidade textual em tempo de execução (caracteres, estimativa de tokens, quebras estruturais e palavras-chave técnicas) e despacha requisições para processadores rápidos ou agentes robustos. Inclui rate limiter in-memory baseado no algoritmo Token Bucket (`429 Too Many Requests` + `Retry-After`) e propagação de Correlation ID.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring WebFlux, Reactor, WebClient.

* **[08 - Simulador de Transações Pix](08-simulador-transacoes-pix):**  
  API de carteira virtual com rigor financeiro (valores estritamente em centavos inteiros), bloqueio pessimista determinístico ordenado por UUID para eliminação de deadlocks, livro-razão imutável (append-only ledger), idempotência estrita via cabeçalho `Idempotency-Key` e padrão Transactional Outbox para eventos de transferências acima do limite de segurança.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring Data JPA, Flyway, H2/PostgreSQL.

* **[07 - Agendamento de Laboratórios e Salas](07-agendamento-laboratorios-salas):**  
  Microsserviço de reservas de recursos físicos acadêmicos com prevenção de sobreposição de intervalos temporais via bloqueio pessimista no banco de dados (`findByIdWithLock`), validações de antecedência/duração e exportação de relatórios semanais de utilização em formato CSV em conformidade com o padrão RFC 4180.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring Data JPA, H2/PostgreSQL.

* **[06 - API de Orquestração de Relatórios para IC](06-api-orquestracao-relatorios-ic):**  
  Microsserviço que processa logs semanais de pesquisa de Iniciação Científica, categoriza atividades de forma inteligente (Pesquisa, Desenvolvimento, Estudo, Reunião, Documentação) e orquestra a geração de relatórios consolidados em Markdown prontos para submissão a agências de fomento.
  - *Stack:* Java 21, Spring Boot 3.4+, Spring Web, Data JPA, H2/PostgreSQL.

* **[05 - Agente Analisador de Editais de IC](analisador-editais-ic):**  
  API Java com Spring Boot que coleta editais públicos, extrai conteúdo com Jsoup e usa inteligência artificial para resumir, analisar elegibilidade e classificar oportunidades de pesquisa conforme o perfil do estudante.
  - *Stack:* Java 21, Spring Boot 3.4+, Jsoup, Spring Data JPA, Gemini Pro / Heurística.

---

### 🧩 Fundamentos e Primeiras APIs (Projetos 01 a 04)

* **[04 - Gerenciamento de Obras, Distribuidoras e Custos](04%20API%20Gerenciamento%20Obras):**  
  API RESTful para análise de dados interligados entre distribuidoras elétricas, obras e custos encadeados.
* **[03 - Analisador de Dados Aneel (CSV)](03%20Analisador%20Dados%20Aneel):**  
  Processador e analisador de arquivos CSV governamentais com filtros e agregação utilizando Java Streams.
* **[02 - API de Gerenciamento RPG](02%20API%20de%20gerenciamento%20RPG):**  
  API RESTful com persistência relacional (PostgreSQL), Swagger/OpenAPI e regras de domínio de RPG.
* **[01 - Mini RPG](01%20Mini%20RPG):**  
  Simulador em linha de comando focado na aplicação pura de Programação Orientada a Objetos (Herança, Polimorfismo, Encapsulamento).

---

## 🛠️ Como Executar os Projetos

Cada microsserviço (projetos 05 a 11) é um projeto Maven autônomo. Para executá-los ou rodar os testes:

```bash
# Entrar na pasta do projeto desejado:
cd 08-simulador-transacoes-pix

# Executar testes unitários e de integração:
./mvnw clean test

# Inicializar o serviço:
./mvnw spring-boot:run
```
