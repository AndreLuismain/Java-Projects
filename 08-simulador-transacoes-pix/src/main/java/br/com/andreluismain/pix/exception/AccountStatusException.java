package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando a conta está inativa, suspensa ou encerrada para operações.
 */
public class AccountStatusException extends PixException {

    public AccountStatusException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "ACCOUNT_INACTIVE");
    }
}
