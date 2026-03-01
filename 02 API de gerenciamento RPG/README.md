# RPG Character Management API

Este projeto consiste em uma **API REST** funcional desenvolvida para o gerenciamento de personagens em um ambiente de RPG. Ele representa a evolução técnica dos meus estudos de **Programação Orientada a Objetos (POO)**, transicionando de aplicações via terminal para um ecossistema Web completo com persistência de dados real.

O desenvolvimento desta API faz parte da minha trajetória no curso de **Bacharelado em Sistemas de Informação (BSI)** no **ICMC - USP**.



## Tecnologias Utilizadas

* **Linguagem:** Java (versões 17 ou 21).
* **Framework Core:** Spring Boot 3.
* **Persistência de Dados:** Spring Data JPA.
* **Banco de Dados:** PostgreSQL (Relacional).
* **Documentação:** Swagger UI (OpenAPI 3).
* **Gerenciador de Dependências:** Maven.

## Funcionalidades (CRUD)

A API permite realizar todas as operações fundamentais de um sistema de gestão de dados:

* **Create (Post):** Cadastro de novos personagens com atributos de nome, classe, nível e pontos de vida.
* **Read (Get):** Listagem completa de todos os personagens persistidos no banco de dados.
* **Update (Put):** Atualização dinâmica de atributos, permitindo a evolução de níveis e status dos personagens.
* **Delete (Delete):** Remoção de registros específicos do banco de dados.

## Como Rodar o Projeto

### Pré-requisitos
1. Possuir o **PostgreSQL** instalado e configurado na máquina local.
2. Configurar as credenciais de acesso (usuário e senha) no arquivo `src/main/resources/application.properties`.
3. Utilizar uma IDE compatível (como IntelliJ IDEA) para rodar o projeto Maven.

### Execução
1. Clone este repositório em sua máquina.
2. Execute a classe principal `ApiApplication.java`.
3. O servidor será iniciado por padrão na porta **8080**.

## Documentação Interativa

A API utiliza o **Swagger** para documentação automática, permitindo testar todas as rotas diretamente pelo navegador, sem necessidade de ferramentas externas como Postman ou Insomnia.

Com a aplicação em execução, acesse:
 `http://localhost:8080/swagger-ui/index.html`

---

## Sobre o Autor

**André Luís** 
* Graduando em **Sistemas de Informação (BSI)**  pelo **ICMC - USP**.
* **Tecnólogo em Redes de Computadores** pelo **SENAI**.
* Foco em desenvolvimento **Fullstack Java** e arquitetura de sistemas escaláveis.
