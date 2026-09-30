package br.com.andreluismain.gateway.dto;

/**
 * Informações sobre cotas de requisições e rate limiting para o cliente.
 */
public record LimitsResponse(
        String clientId,
        int capacity,
        int availableTokens,
        int refillRatePerSecond,
        int resetIntervalSeconds
) {}
