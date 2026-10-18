package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando a mesma chave de idempotência é reutilizada com parâmetros divergentes.
 */
public class IdempotencyConflictException extends PixException {

    public IdempotencyConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT");
    }
}
