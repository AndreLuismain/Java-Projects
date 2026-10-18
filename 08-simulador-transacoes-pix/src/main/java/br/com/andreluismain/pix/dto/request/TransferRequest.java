package br.com.andreluismain.pix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

/**
 * Payload de requisição para transferência Pix.
 */
public record TransferRequest(
        @NotNull(message = "Conta de origem é obrigatória")
        UUID sourceAccountId,

        @NotNull(message = "Conta de destino é obrigatória")
        UUID destinationAccountId,

        @NotNull(message = "Valor da transferência é obrigatório")
        @Positive(message = "Valor deve ser estritamente maior que zero")
        Long amountInCents
) {}
