package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando uma conta não é localizada pelo identificador ou chave Pix.
 */
public class AccountNotFoundException extends PixException {

    public AccountNotFoundException(UUID id) {
        super("Conta com id '" + id + "' não encontrada", HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND");
    }

    public AccountNotFoundException(String pixKey) {
        super("Conta com chave Pix informada não encontrada", HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND");
    }
}
