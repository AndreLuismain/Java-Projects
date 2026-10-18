package br.com.andreluismain.relatoriosic.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando ocorre conflito (ex: semana duplicada para o mesmo projeto).
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "CONFLICT");
    }
}
