# Especificacao Tecnica - Agente Documentador de Codigo Legado

## Objetivo

Analisar arquivos de codigo-fonte antigos e gerar documentacao tecnica Markdown sobre finalidade, fluxo, dependencias, entradas, saidas, riscos e pontos de manutencao.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/documentador/
  controller/
  dto/request/
  dto/response/
  domain/model/
  domain/service/
  repository/
  integration/llm/
  extraction/
  markdown/
  exception/
  config/
src/main/resources/db/migration/
src/test/java/br/com/andreluismain/documentador/
  extraction/
  integration/
```

## Dados

`Analysis`: UUID, fileName, language, sizeBytes, contentHash, status `RECEIVED`, `PROCESSING`, `COMPLETED` ou `FAILED`, createdAt e completedAt.

`GeneratedDocument`: UUID, analysisId, markdown, modelName, promptVersion, createdAt e contentHash.

`AnalysisError`: UUID, analysisId, code, messageSafe, createdAt.

## Fluxo

1. Validar multipart, extensao permitida, tamanho e encoding.
2. Armazenar o conteudo original fora do banco ou em coluna apropriada, com hash de integridade.
3. Identificar linguagem e extrair metadados sem executar o codigo.
4. Montar prompt com instrucoes fixas e conteudo limitado.
5. Validar a resposta do modelo e gerar Markdown com secoes padronizadas.
6. Persistir versao do documento e disponibilizar consulta assincrona.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/analyses` | Multipart `file` e `language` opcional | `{ "id": "uuid", "status": "PROCESSING" }` | 202, 400, 413, 415 |
| GET | `/api/v1/analyses/{id}` | Parametro de rota | Metadados e status | 200, 404 |
| GET | `/api/v1/analyses/{id}/document` | Query `format=markdown` | Documento `text/markdown` | 200, 404, 409 |
| GET | `/api/v1/analyses` | Query `language`, `status`, `page`, `size` | Historico paginado | 200 |
| DELETE | `/api/v1/analyses/{id}` | Parametro de rota | Sem corpo | 204, 404 |

## Estrutura minima do Markdown

- Titulo e identificacao do arquivo.
- Resumo executivo.
- Linguagem, ponto de entrada e dependencias.
- Fluxo principal e estruturas de dados.
- Entradas, saidas e efeitos colaterais.
- Riscos, dividas tecnicas e perguntas em aberto.
- Evidencias com referencia a arquivo e linha quando disponiveis.

## Regras de seguranca

- Nunca executar, compilar ou importar o arquivo recebido.
- Bloquear path traversal, arquivos ocultos, binarios, links simbolicos e extensoes nao permitidas.
- Limitar quantidade de arquivos, tamanho individual e tamanho total por analise.
- O conteudo recebido e nao confiavel e nao pode substituir as instrucoes do agente.
- Remover credenciais aparentes antes de enviar ao LLM e nunca registrar o prompt completo.
- Exigir autenticacao para upload, consulta e exclusao.
- Exclusao deve preservar auditoria minima e respeitar politica de retencao.

## Criterios de aceite

- Arquivo invalido e rejeitado com erro estruturado.
- O mesmo hash pode ser reutilizado somente quando a versao do prompt e do modelo forem compativeis.
- Documento incompleto ou resposta invalida nao recebe status `COMPLETED`.
- O historico permite consultar versoes sem sobrescrever documentos anteriores.
- Testes cobrem upload, limites, path traversal, parsing, falha do LLM e Markdown.
