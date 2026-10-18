package br.com.andreluismain.gateway.integration;

import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * Contrato comum para clientes de processamento de texto encaminhados pelo gateway.
 */
public interface ProcessorClient {

    /**
     * Encaminha o texto para o processador de destino de forma não bloqueante.
     *
     * @param correlationId identificador para rastreabilidade distribuída
     * @param text conteúdo textual
     * @return Mono contendo o mapa de resultados estruturados
     */
    Mono<Map<String, Object>> process(String correlationId, String text);
}
