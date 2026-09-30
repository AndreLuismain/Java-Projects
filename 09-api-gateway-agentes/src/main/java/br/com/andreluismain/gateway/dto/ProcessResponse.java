package br.com.andreluismain.gateway.dto;

import java.util.Map;
import java.util.UUID;

/**
 * Resposta unificada do processamento encaminhado pelo gateway.
 */
public record ProcessResponse(
        UUID requestId,
        String correlationId,
        RouteType route,
        int complexityScore,
        String status,
        Map<String, Object> result
) {}
