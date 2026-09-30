package br.com.andreluismain.pix.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento disparado quando uma transação Pix excede o limite de segurança estabelecido.
 */
public record PixSecurityLimitEvent(
        UUID eventId,
        UUID transferId,
        Long amountInCents,
        UUID sourceAccountId,
        LocalDateTime createdAt,
        String reason
) {
    public static final String EVENT_TYPE = "PIX_TRANSFER_ABOVE_SECURITY_LIMIT";

    public static PixSecurityLimitEvent of(UUID transferId, Long amountInCents, UUID sourceAccountId, String reason) {
        return new PixSecurityLimitEvent(
                UUID.randomUUID(),
                transferId,
                amountInCents,
                sourceAccountId,
                LocalDateTime.now(),
                reason
        );
    }
}
