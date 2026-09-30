package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando a conta de origem não possui saldo suficiente.
 */
public class InsufficientBalanceException extends PixException {

    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_BALANCE");
    }
}
