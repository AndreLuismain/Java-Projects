package br.com.andreluismain.documentador.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando o arquivo enviado é inválido, inseguro ou excede limites.
 */
public class InvalidFileException extends DocumentadorException {

    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_FILE");
    }
}
