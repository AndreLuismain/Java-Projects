package br.com.andreluismain.agendamento.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando regras de antecedência ou duração da reserva são violadas.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_REQUEST");
    }
}
