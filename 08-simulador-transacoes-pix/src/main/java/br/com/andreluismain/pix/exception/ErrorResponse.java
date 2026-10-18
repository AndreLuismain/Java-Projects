package br.com.andreluismain.pix.exception;

import java.time.LocalDateTime;

/**
 * Resposta de erro padronizada para a API do Simulador Pix.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {}
