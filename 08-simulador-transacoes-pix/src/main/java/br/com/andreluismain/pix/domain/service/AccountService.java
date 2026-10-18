package br.com.andreluismain.pix.domain.service;

import br.com.andreluismain.pix.domain.model.WalletAccount;
import br.com.andreluismain.pix.dto.request.CreateAccountRequest;
import br.com.andreluismain.pix.dto.response.AccountResponse;
import br.com.andreluismain.pix.exception.AccountNotFoundException;
import br.com.andreluismain.pix.exception.PixException;
import br.com.andreluismain.pix.repository.WalletAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento e abertura de contas de carteira.
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final WalletAccountRepository accountRepository;

    public AccountService(WalletAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Cria uma nova conta na carteira virtual.
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (accountRepository.existsByPixKey(request.pixKey())) {
            throw new PixException("Chave Pix já cadastrada no sistema", HttpStatus.CONFLICT, "PIX_KEY_ALREADY_EXISTS");
        }

        WalletAccount account = new WalletAccount(
                UUID.randomUUID(),
                request.ownerId(),
                request.pixKey(),
                request.initialBalanceInCents() != null ? request.initialBalanceInCents() : 0L,
                request.currency()
        );

        WalletAccount saved = accountRepository.save(account);
        log.info("Conta criada com sucesso: id={}, ownerId={}, keyMasked={}",
                saved.getId(), saved.getOwnerId(), maskPixKey(saved.getPixKey()));

        return AccountResponse.from(saved);
    }

    /**
     * Localiza conta por ID.
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID id) {
        return accountRepository.findById(id)
                .map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * Mascara chaves Pix para proteção de dados em logs e exibição pública.
     */
    public static String maskPixKey(String pixKey) {
        if (pixKey == null || pixKey.length() <= 4) {
            return "****";
        }
        int len = pixKey.length();
        int visible = Math.min(2, len / 4);
        return pixKey.substring(0, visible) + "***" + pixKey.substring(len - visible);
    }
}
