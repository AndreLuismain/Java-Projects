package br.com.andreluismain.documentador.exception;

import java.time.LocalDateTime;

/**
 * Payload estruturado de erro retornado pela API do Documentador.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {}
