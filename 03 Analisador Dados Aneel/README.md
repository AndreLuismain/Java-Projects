# Analisador de Dados da ANEEL (Java 21)

Um processador robusto e genérico de arquivos CSV desenvolvido em **Java 21**, focado em analisar dados reais de usinas geradoras do Brasil disponibilizados pela ANEEL.

Este projeto foi construído aplicando conceitos avançados de Orientação a Objetos, Clean Code e as mais recentes features da linguagem Java.

## Tecnologias e Recursos Utilizados

- **Java 21**: Uso de *Pattern Matching para switch* para eliminar casts verbosos e trazer mais segurança na tipagem.
- **Records & Sealed Interfaces**: Modelagem de domínio imutável e controle estrito de herança (Fail-fast strategy).
- **Generics `<T>` & Programação Funcional**: Leitor de CSV genérico com injeção de função (`Function<String, Optional<T>>`), garantindo flexibilidade para ler qualquer tipo de arquivo no futuro.
- **Stream API & Lambdas**: Processamento, filtragem e agrupamento de dados em memória de forma performática.
- **Estruturas de Dados**: Uso de `TreeMap` para ordenação automática e `LinkedList` para histórico.
- **Exceções Customizadas**: Tratamento de erros de negócio (`Checked` e `Unchecked`) com *Try-with-resources* para evitar vazamento de memória.

## Arquitetura do Sistema

O sistema é dividido nas seguintes responsabilidades:
1. **Domínio:** Uso de `BaseDeDados` (Record) e categorização via interface selada `FonteEnergia` (`FonteRenovavel` e `FonteNaoRenovavel`).
2. **Infraestrutura:** Classe genérica `LeitorCsv` agnóstica às regras de negócio.
3. **Controle/Terminal:** Menu interativo para o usuário realizar buscas e extrair relatórios das potências por ano e tipo de geração.

## Como Executar

1. Clone o repositório.
2. Certifique-se de ter a **JDK 21** ou superior instalada.
3. Compile e execute a classe `central.java`.
4. O sistema solicitará o caminho do arquivo `.csv`. (Utilize o arquivo de amostra fornecido no projeto).
