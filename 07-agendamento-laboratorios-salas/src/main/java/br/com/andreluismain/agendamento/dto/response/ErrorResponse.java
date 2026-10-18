package br.com.andreluismain.agendamento.dto.response;

import java.time.LocalDateTime;

/**
 * Resposta de erro padronizada da API de agendamento de recursos.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {}
