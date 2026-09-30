# Meus Projetos em Java

Bem-vindo ao meu repositório central de projetos e estudos práticos em **Java**!

Este espaço é dedicado a documentar minha evolução na linguagem e a construção contínua de uma base técnica sólida para desenvolvimento **Backend**, sistemas distribuídos e integração com **Inteligência Artificial**. Aqui você encontrará desde fundamentos essenciais de computação até APIs corporativas robustas e prontas para produção.

---

## 🛠️ Tecnologias e Conceitos Dominados

- **Java Moderno (Java 21 LTS):** Records, Pattern Matching, Streams, lambdas e concurrency.
- **Ecossistema Spring:** Spring Boot 3+, Spring Web, Spring WebFlux (chamadas assíncronas e reativas), Spring Data JPA e Bean Validation.
- **Inteligência Artificial Generativa:** Integração com **Google Gemini Pro** via APIs REST para análise semântica, sumarização e classificação com engenharia de prompts defensiva.
- **Segurança de Aplicações:** Mitigação de vulnerabilidades como **SSRF** (Server-Side Request Forgery), sanitização de HTML/DOM e validação de payloads.
- **Bancos de Dados & Migrações:** PostgreSQL, versionamento de schemas com **Flyway** e testes em memória com H2.
- **Web Scraping & Normalização:** Extração de conteúdo estruturado com **Jsoup** e deduplicação via hashing criptográfico (SHA-256).
- **Engenharia de Software:** Arquitetura em camadas, princípios SOLID, Clean Code e documentação viva com **OpenAPI (Swagger UI)**.
- **Testes Automatizados:** Testes unitários e de integração com **JUnit 5**, **Mockito**, **WebMvcTest** e **Testcontainers**.

---

## 🚀 Portfólio de Projetos

### [05 - Agente Analisador de Editais de IC](analisador-editais-ic)
> **Stack:** Java 21 | Spring Boot 3.4 | Spring WebFlux | PostgreSQL | Flyway | Google Gemini API | Jsoup | Docker | OpenAPI
* API REST inteligente projetada para coletar editais e oportunidades acadêmicas (PIBIC, bolsas e estágios) na web via web scraping assíncrono.
* Realiza a sanitização de conteúdo HTML com Jsoup, protege contra ataques SSRF com inspeção rigorosa de DNS/IPs privados e calcula hashes SHA-256 para deduplicação.
* Integra-se ao **Google Gemini Pro** com contratos estritos em JSON para sintetizar requisitos, identificar riscos e classificar a compatibilidade (`HIGH_MATCH`, `MEDIUM_MATCH`, etc.) com o perfil do estudante/pesquisador.
* Inclui suporte a processamento em lote (Batch Jobs), execução em background via thread pools configurados e suíte com 18 testes automatizados aprovados.

---

### [04 - Gerenciamento de Obras, Distribuidoras e Custos](04%20API%20Gerenciamento%20Obras)
> **Stack:** Java 21 | Spring Boot | Spring Data JPA | PostgreSQL | Flyway | OpenAPI / Swagger
* API RESTful voltada para o setor de infraestrutura e distribuição, correlacionando dados de distribuidoras elétricas, obras e centros de custo em fluxo analítico encadeado.
* Implementa controle de transações, validações de integridade relacional e documentação Swagger interativa.

---

### [03 - Analisador Dados Aneel (CSV)](03%20Analisador%20Dados%20Aneel)
> **Stack:** Java | Streams API | File I/O | Manipulação de Dados
* Ferramenta analítica de alta performance para processamento e filtragem de grandes volumes de dados públicos da ANEEL em formato CSV.
* Demonstra uso avançado de Java Streams, predicates customizados e manipulação eficiente de I/O em disco.

---

### [02 - Mini RPG API](02%20API%20de%20gerenciamento%20RPG)
> **Stack:** Java | Spring Boot | Spring Data JPA | PostgreSQL | Swagger
* Evolução do sistema de RPG para o ecossistema web, expondo endpoints REST para controle de personagens, inventários e sessões.
* Persistência em banco de dados relacional e documentação interativa.

---

### [01 - Mini RPG](01%20Mini%20RPG)
> **Stack:** Java Core | POO | Algoritmos
* Sistema de batalha em turnos simulando mecânicas de RPG de mesa.
* Focado em demonstrar na prática os pilares da Programação Orientada a Objetos: **Herança**, **Polimorfismo**, **Encapsulamento** e **Abstração**.

---

## 📦 Como Clonar e Executar

```bash
# Clonar o repositório
git clone https://github.com/AndreLuismain/Java-Projects.git

# Acessar a pasta
cd Java-Projects
```

Para instruções específicas de execução de cada projeto (incluindo subida de banco via Docker e execução com Maven Wrapper), consulte o arquivo `README.md` localizado no diretório de cada aplicação.
