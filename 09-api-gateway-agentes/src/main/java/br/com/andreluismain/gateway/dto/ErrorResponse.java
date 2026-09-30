package br.com.andreluismain.gateway.dto;

import java.time.LocalDateTime;

/**
 * Payload de erro padrão retornado pelo gateway.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String correlationId
) {}
