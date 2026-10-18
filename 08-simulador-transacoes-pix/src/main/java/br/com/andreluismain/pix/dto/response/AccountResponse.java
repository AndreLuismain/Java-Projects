package br.com.andreluismain.pix.dto.response;

import br.com.andreluismain.pix.domain.model.AccountStatus;
import br.com.andreluismain.pix.domain.model.WalletAccount;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta contendo informações públicas da conta de carteira.
 */
public record AccountResponse(
        UUID id,
        UUID ownerId,
        String pixKey,
        Long balanceInCents,
        String currency,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AccountResponse from(WalletAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getOwnerId(),
                account.getPixKey(),
                account.getBalanceInCents(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
