# Mini RPG API

Esta é uma API REST desenvolvida para o gerenciamento de personagens e simulação de mecânicas de RPG. O projeto utiliza tecnologias de mercado para garantir persistência de dados, escalabilidade via containers e documentação automatizada.

## Links do Projeto
* **Homepage da Aplicação:** https://java-projects-y09s.onrender.com
* **Documentação Swagger:** https://java-projects-y09s.onrender.com/swagger-ui/index.html

## Funcionalidades
* **CRUD de Personagens:** Operações completas de criação, leitura, atualização e exclusão de personagens.
* **Mecânicas de RPG:** Gerenciamento de atributos como força, defesa e pontos de vida.
* **Segurança de Dados:** Implementação de DTOs (Data Transfer Objects) para isolar a camada de persistência da camada de apresentação.
* **Documentação Interativa:** Interface Swagger/OpenAPI para testes de endpoints em tempo real.
* **Persistência Relacional:** Banco de dados PostgreSQL configurado para ambiente de produção no Render.

## Tecnologias Utilizadas
* **Java 17:** Versão de longo suporte (LTS) utilizada para o desenvolvimento da lógica.
* **Spring Boot 3.3.4:** Framework para construção da arquitetura backend.
* **Spring Data JPA:** Abstração para manipulação do banco de dados e mapeamento objeto-relacional.
* **PostgreSQL:** Banco de dados relacional utilizado para persistência em nuvem.
* **Docker:** Containerização da aplicação para garantir paridade entre os ambientes de desenvolvimento e produção.
* **Lombok:** Biblioteca para redução de código repetitivo e aumento da produtividade.
* **Render:** Plataforma Cloud utilizada para o deploy da aplicação e do banco de dados através de infraestrutura como código (Dockerfile).

## Estrutura de Camadas
A aplicação segue o padrão de arquitetura em camadas para garantir a separação de responsabilidades:
1. **Controller:** Responsável por expor os endpoints e gerenciar as requisições HTTP.
2. **Service:** Camada que contém as regras de negócio e a lógica de processamento do RPG.
3. **Repository:** Interface que estende o JPARepository para comunicação direta com o banco de dados.
4. **Model/Entity:** Representação das entidades do domínio mapeadas para tabelas do PostgreSQL.
5. **DTO:** Objetos de transferência de dados utilizados para garantir a integridade da API.

## Como Executar o Projeto Localmente

### Pré-requisitos
* JDK 17
* Maven 3.x
* Docker (opcional)

### Passo a Passo
1. Clone este repositório:
   git clone https://github.com/AndreLuismain/Java-Projects.git

2. Acesse o diretório do projeto:
   cd "02 API de gerenciamento RPG/api"

3. Compile o projeto e gere o arquivo executável:
   mvn clean install

4. Execute a aplicação:
   mvn spring-boot:run

A API estará disponível em http://localhost:8080.

## Autor
André Luís

Estudante de Sistemas de Informação - ICMC/USP

GitHub: https://github.com/AndreLuismain
