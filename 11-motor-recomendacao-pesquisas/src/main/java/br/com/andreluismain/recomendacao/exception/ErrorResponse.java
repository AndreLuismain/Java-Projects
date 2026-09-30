package br.com.andreluismain.recomendacao.exception;

import java.time.LocalDateTime;

/**
 * Resposta de erro padronizada do motor de recomendação.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {}
