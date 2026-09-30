package br.com.andreluismain.pix.dto.response;

import br.com.andreluismain.pix.domain.model.LedgerEntry;
import br.com.andreluismain.pix.domain.model.LedgerEntryType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta de item do extrato bancário (ledger).
 */
public record LedgerEntryResponse(
        UUID id,
        UUID transferId,
        UUID accountId,
        LedgerEntryType entryType,
        Long amountInCents,
        Long balanceAfterInCents,
        LocalDateTime createdAt
) {
    public static LedgerEntryResponse from(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.getId(),
                entry.getTransferId(),
                entry.getAccountId(),
                entry.getEntryType(),
                entry.getAmountInCents(),
                entry.getBalanceAfterInCents(),
                entry.getCreatedAt()
        );
    }
}
