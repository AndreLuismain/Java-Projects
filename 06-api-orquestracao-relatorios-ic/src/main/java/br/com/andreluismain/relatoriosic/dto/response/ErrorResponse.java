package br.com.andreluismain.relatoriosic.dto.response;

import java.time.LocalDateTime;

/**
 * Resposta estruturada de erro para a API de orquestração de relatórios.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId
) {}
