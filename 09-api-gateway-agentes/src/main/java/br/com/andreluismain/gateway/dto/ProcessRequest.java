package br.com.andreluismain.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Payload de entrada para processamento de texto pelo gateway de agentes.
 */
public record ProcessRequest(
        UUID requestId,

        @NotBlank(message = "O texto a ser processado é obrigatório")
        String text,

        ProcessingMode mode
) {
    public ProcessingMode getEffectiveMode() {
        return mode != null ? mode : ProcessingMode.AUTO;
    }
}
