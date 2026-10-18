package br.com.andreluismain.pix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * Payload de requisição para criação de conta de carteira.
 */
public record CreateAccountRequest(
        @NotNull(message = "Identificador do proprietário (ownerId) é obrigatório")
        UUID ownerId,

        @NotBlank(message = "Chave Pix é obrigatória")
        String pixKey,

        String currency,

        @PositiveOrZero(message = "Saldo inicial não pode ser negativo")
        Long initialBalanceInCents
) {}
