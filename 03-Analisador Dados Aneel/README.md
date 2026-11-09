# 03 · Analisador de Dados da ANEEL (Java 21 & Programação Funcional)

Processador e analisador interativo de arquivos CSV desenvolvido em **Java 21 LTS**, projetado para ingerir, higienizar e agregar dados públicos de usinas geradoras de energia elétrica disponibilizados pela **ANEEL**.

---

## 1. Objetivo do Projeto

Realizar a leitura resiliente de bases tabulares brutas (`.csv`) contendo dados de geração elétrica, aplicando parsing manual tolerante a vírgulas internas entre aspas, validação *fail-fast* por linha e agregações analíticas em memória via **Java Stream API**.

---

## 2. Conceitos e Recursos do Java 21 Aplicados

* **Records & Imutabilidade:** Modelagem de domínio imutável com `BaseDeDados`, `FonteRenovavel` e `FonteNaoRenovavel`.
* **Sealed Interfaces (`sealed` / `permits`):** Controle estrito da hierarquia de tipos de energia através da interface selada `FonteEnergia`.
* **Pattern Matching for `switch` (Java 21):** Desestruturação segura de tipos (`case FonteRenovavel r -> ...`, `case FonteNaoRenovavel n -> ...`) sem necessidade de *downcasting* manual.
* **Generics `<T>` e Alta Ordem:** O componente `LeitorCsv` é desacoplado do domínio e recebe a função de mapeamento `Function<String, Optional<T>>` (`central::converterLinha`).
* **Stream API & Coleções Ordenadas:**
  * Agrupamento e soma de potência instalada (`BigDecimal`) por ano utilizando `Collectors.groupingBy` com `TreeMap::new` e `Collectors.reducing`.
  * Filtragem por sigla de geração (`EOL`, `UFV`, `CGH`, etc.) e redução monetária/numérica exata com `BigDecimal`.
  * Histórico de comandos recentes limitado aos últimos 5 registros com `LinkedList<Integer>`.
* **Tratamento de Exceções (`Checked` e `Unchecked`):**
  * `ArquivoInvalidoException` (*checked*) para falhas de abertura de arquivo com *try-with-resources* (`BufferedReader`).
  * `LinhaCorrompidaException` (*unchecked*) para descarte resiliente de registros malformados sem interromper o processamento do lote.

---

## 3. Estrutura de Arquivos

```text
03 Analisador Dados Aneel/
├── README.md
└── src/
    ├── central.java                   # CLI interativa, parser CSV e consultas Stream API
    ├── LeitorCsv.java                 # Leitor genérico de arquivos CSV com Function<String, Optional<T>>
    ├── BaseDeDados.java               # Record de domínio representando o registro da ANEEL
    ├── FonteEnergia.java              # Sealed interface e records FonteRenovavel / FonteNaoRenovavel
    ├── ArquivoInvalidoException.java  # Exceção checked para erros de I/O de arquivo
    ├── LinhaCorrompidaException.java  # Exceção unchecked para linhas com colunas insuficientes
    └── 234.csv                        # Base amostral de usinas geradoras para testes locais
```

---

## 4. Como Compilar e Executar

Requer **JDK 21** instalado. A partir da pasta `03 Analisador Dados Aneel`:

```powershell
cd "03 Analisador Dados Aneel/src"
javac *.java
java central
```

Quando solicitado pelo prompt interativo, informe o arquivo de amostra:
```text
Digite o caminho do arquivo CSV (ex: src/234.csv): 234.csv
```
*(Ou `src/234.csv` caso execute a partir do diretório pai `03 Analisador Dados Aneel`).*
