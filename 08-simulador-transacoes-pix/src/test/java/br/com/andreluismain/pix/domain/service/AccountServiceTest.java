package br.com.andreluismain.pix.domain.service;

import br.com.andreluismain.pix.domain.model.AccountStatus;
import br.com.andreluismain.pix.domain.model.WalletAccount;
import br.com.andreluismain.pix.dto.request.CreateAccountRequest;
import br.com.andreluismain.pix.dto.response.AccountResponse;
import br.com.andreluismain.pix.exception.AccountNotFoundException;
import br.com.andreluismain.pix.exception.PixException;
import br.com.andreluismain.pix.repository.WalletAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private WalletAccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    @DisplayName("Deve criar conta com sucesso quando chave Pix não existir")
    void shouldCreateAccountSuccessfully() {
        UUID ownerId = UUID.randomUUID();
        CreateAccountRequest request = new CreateAccountRequest(ownerId, "teste@pix.com", "BRL", 5000L);

        when(accountRepository.existsByPixKey("teste@pix.com")).thenReturn(false);
        when(accountRepository.save(any(WalletAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(ownerId, response.ownerId());
        assertEquals("teste@pix.com", response.pixKey());
        assertEquals(5000L, response.balanceInCents());
        assertEquals(AccountStatus.ACTIVE, response.status());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar conta com chave Pix já cadastrada")
    void shouldThrowConflictWhenPixKeyAlreadyExists() {
        CreateAccountRequest request = new CreateAccountRequest(UUID.randomUUID(), "existente@pix.com", "BRL", 1000L);

        when(accountRepository.existsByPixKey("existente@pix.com")).thenReturn(true);

        assertThrows(PixException.class, () -> accountService.createAccount(request));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve mascarar chaves Pix adequadamente")
    void shouldMaskPixKeyProperly() {
        assertEquals("****", AccountService.maskPixKey("cpf"));
        String masked = AccountService.maskPixKey("user@email.com");
        assertTrue(masked.contains("***"));
    }

    @Test
    @DisplayName("Deve lançar AccountNotFoundException para conta inexistente")
    void shouldThrowNotFoundForNonExistentAccount() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(id));
    }
}
