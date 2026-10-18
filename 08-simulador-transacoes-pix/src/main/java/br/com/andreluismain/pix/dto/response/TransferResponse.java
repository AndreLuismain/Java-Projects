package br.com.andreluismain.pix.dto.response;

import br.com.andreluismain.pix.domain.model.Transfer;
import br.com.andreluismain.pix.domain.model.TransferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta com os detalhes de uma transferência Pix executada.
 */
public record TransferResponse(
        UUID id,
        String idempotencyKey,
        UUID sourceAccountId,
        UUID destinationAccountId,
        Long amountInCents,
        TransferStatus status,
        String correlationId,
        LocalDateTime createdAt
) {
    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getIdempotencyKey(),
                transfer.getSourceAccountId(),
                transfer.getDestinationAccountId(),
                transfer.getAmountInCents(),
                transfer.getStatus(),
                transfer.getCorrelationId(),
                transfer.getCreatedAt()
        );
    }
}
