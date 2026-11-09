package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando os dados de entrada são semanticamente inválidos ou violam premissas de negócio.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
    }
}
