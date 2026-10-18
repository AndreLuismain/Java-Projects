package br.com.andreluismain.pix.domain.service;

import br.com.andreluismain.pix.dto.response.LedgerEntryResponse;
import br.com.andreluismain.pix.exception.AccountNotFoundException;
import br.com.andreluismain.pix.repository.LedgerEntryRepository;
import br.com.andreluismain.pix.repository.WalletAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço responsável por consultas ao extrato de movimentações contábeis.
 */
@Service
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final WalletAccountRepository accountRepository;

    public LedgerService(LedgerEntryRepository ledgerEntryRepository, WalletAccountRepository accountRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Consulta o extrato de uma conta de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<LedgerEntryResponse> getStatement(UUID accountId, Pageable pageable) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return ledgerEntryRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
                .map(LedgerEntryResponse::from);
    }
}
