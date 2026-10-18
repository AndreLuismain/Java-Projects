# 04 · API de Gerenciamento de Obras, Distribuidoras e Custos

API RESTful desenvolvida em **Java 21** e **Spring Boot 3** para o gerenciamento de concessionárias distribuidoras de energia, obras de infraestrutura elétrica e discriminação de itens de custo, utilizando **PostgreSQL**, **Flyway** e projeções otimizadas no banco de dados.

---

## 1. Visão Geral e Problema Resolvido

Sistemas de gestão de infraestrutura elétrica exigem integridade referencial entre distribuidoras, obras e seus respectivos itens de custo (materiais, mão de obra, serviços), além de relatórios agregados de alto desempenho. Este projeto implementa uma arquitetura em camadas limpa que isola contratos HTTP via Java Records (DTOs), aplica migrações determinísticas de schema com Flyway e delega agregações financeiras diretamente ao motor do PostgreSQL via projeções JPA.

---

## 2. Tecnologias Utilizadas

* **Linguagem:** Java 21 LTS
* **Framework:** Spring Boot 3 (Spring Web, Spring Data JPA, Jakarta Bean Validation)
* **Banco de Dados:** PostgreSQL (orquestrado localmente via Docker Compose)
* **Versionamento de Schema:** Flyway Migration (`V1__criar_tabelas_iniciais.sql`)
* **Produtividade & Testes:** Lombok, Maven Wrapper (`mvnw`), JUnit 5

---

## 3. Arquitetura e Decisões Técnicas

1. **Separação Estrita de Responsabilidades (Layered Architecture):**
   * **Controllers (`DistribuidoraController`, `ObraController`, `RelatorioController`):** Focados exclusivamente em roteamento HTTP, status codes semânticos (`201 Created` com header `Location`, `200 OK`, `204 No Content`) e delegação. Não contêm blocos `try-catch` nem lógica de negócio.
   * **Services (`DistribuidoraService`, `ObraService`, `CustosService`, `RelatorioService`):** Concentram validações de domínio e transformações funcionais com `Stream API`.
   * **Repositories (`DistribuidoraRepository`, `ObraRepository`, `ItemCustoRepository`):** Persistência JPA e consultas customizadas.
2. **Padrão DTO com Java Records:**
   * Isolamento das entidades JPA (`@Entity`) em relação aos payloads de entrada e saída, prevenindo exposição indevida do modelo relacional.
3. **Tratamento Global de Exceções (`@RestControllerAdvice`):**
   * Centralizado em `TratadorDeErrosGlobais`, convertendo erros de negócio e falhas de validação (`@Valid`) em respostas JSON padronizadas (`ErroPadrao`).
4. **Agregação no SGBD via Projeções (`CustoPorDistribuidoraProjection`):**
   * O cálculo do custo total agrupado por distribuidora é executado no PostgreSQL via `@Query`, evitando o carregamento de grafos completos de entidades na memória da JVM.

---

## 4. Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/distribuidoras` | Lista todas as distribuidoras cadastradas. |
| `POST` | `/api/distribuidoras` | Cadastra uma nova distribuidora com validação Jakarta (`@Valid`). |
| `GET` | `/api/obras?distribuidoraId={id}&custoMin={valor}` | Lista obras de uma distribuidora com filtro opcional de custo mínimo. |
| `POST` | `/api/obras` | Registra uma nova obra vinculada a uma distribuidora e retorna a URI no cabeçalho `Location`. |
| `GET` | `/api/obras/{id}/custos` | Lista os itens de custo (`ItemCusto`) vinculados a uma obra específica. |
| `GET` | `/api/relatorios/custo-por-distribuidora` | Retorna a projeção agregada de custos totais por concessionária distribuidora. |

---

## 5. Como Executar Localmente

### Pré-requisitos
* **JDK 21**
* **Docker** e **Docker Compose**

### Passo a Passo
1. Acesse o diretório do projeto e inicie o container do PostgreSQL:
   ```powershell
   cd "04 API Gerenciamento Obras"
   docker-compose up -d
   ```
2. Compile e execute a aplicação via Maven Wrapper (as migrações do Flyway serão aplicadas automaticamente na inicialização):
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
3. Execute os testes automatizados:
   ```powershell
   .\mvnw.cmd test
   ```

A API estará disponível em `http://localhost:8080`.
