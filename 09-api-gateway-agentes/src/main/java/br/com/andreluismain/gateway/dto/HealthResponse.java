package br.com.andreluismain.gateway.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Resposta do endpoint de health check do gateway.
 */
public record HealthResponse(
        String status,
        LocalDateTime timestamp,
        Map<String, Object> components
) {}
