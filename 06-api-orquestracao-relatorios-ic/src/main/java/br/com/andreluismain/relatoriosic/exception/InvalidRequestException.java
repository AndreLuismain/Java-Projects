package br.com.andreluismain.relatoriosic.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando dados da requisição violam regras de datas ou preenchimento.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
    }
}
