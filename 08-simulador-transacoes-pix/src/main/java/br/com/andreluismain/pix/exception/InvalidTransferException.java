package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando regras estruturais da transferência são violadas (ex: mesma conta origem e destino).
 */
public class InvalidTransferException extends PixException {

    public InvalidTransferException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_TRANSFER");
    }
}
