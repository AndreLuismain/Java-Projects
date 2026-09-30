# Especificacao Tecnica - API Gateway de Agentes

## Objetivo

Receber texto, classificar sua complexidade com regras deterministicas e encaminhar o pedido para um processador rapido ou para um agente robusto, sem expor diretamente os servicos internos.

## Arquitetura de pastas

```text
src/main/java/br/com/andreluismain/gateway/
  controller/
  dto/
  routing/
  rate_limit/
  integration/fast/
  integration/robust/
  filter/
  exception/
  config/
src/test/java/br/com/andreluismain/gateway/
```

## Contrato de entrada

```json
{
  "requestId": "uuid opcional",
  "text": "conteudo a processar",
  "mode": "AUTO"
}
```

`mode` aceita `AUTO`, `FAST` e `ROBUST`. O modo manual nao pode permitir que o cliente ignore politicas de seguranca ou limites de tamanho.

## Roteamento

Complexidade inicial: tamanho em caracteres, quantidade de tokens aproximada, quantidade de secoes, pontuacao de linguagem e palavras-chave de dominio. O limiar deve ser configuravel. Em `AUTO`, textos abaixo do limiar vao para o processador rapido; os demais vao para o fluxo robusto.

A decisao deve retornar `route`, `complexityScore`, `correlationId` e o resultado do destino.

## Endpoints

| Metodo | Rota | Request | Response | Status |
|---|---|---|---|---|
| POST | `/api/v1/process` | JSON com `text`, `requestId` e `mode` | Resultado normalizado e rota usada | 200, 400, 429, 502, 504 |
| GET | `/api/v1/health` | Sem corpo | Status do gateway | 200, 503 |
| GET | `/api/v1/limits` | Header de cliente | Cotas restantes e janela | 200, 401 |

Erro padrao: `timestamp`, `status`, `code`, `message`, `correlationId`.

## Rate limiting

Aplicar limite por API key ou identidade autenticada. Em instalacao unica, Bucket4j em memoria e aceitavel; em varias instancias, usar Redis ou outro armazenamento atomico compartilhado. Rejeicoes retornam 429 e `Retry-After`.

## Regras de negocio e seguranca

- Texto obrigatorio, com limites minimo e maximo configuraveis.
- Rejeitar payloads desconhecidos quando a politica estiver em modo estrito.
- Nao encaminhar headers sensiveis para os destinos.
- Definir timeout, retry apenas para erros seguros e circuit breaker por destino.
- Nao fazer fallback automatico que duplique efeitos colaterais sem idempotencia.
- Propagar correlation ID e registrar metricas de latencia, rota e rejeicoes.
- Sanitizar logs e nao persistir o texto completo por padrao.

## Criterios de aceite

- Payload invalido e rejeitado antes do rate limit e do encaminhamento.
- O mesmo texto produz a mesma rota para a mesma configuracao.
- O limite retorna 429 sem chamar qualquer processador.
- Falha do processador rapido produz erro estruturado, sem vazar stack trace.
- Testes cobrem limiares, limites, timeout, circuit breaker e headers.
