package br.com.andreluismain.agendamento.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando existe conflito de horário/sobreposição para o recurso desejado.
 */
public class ReservationConflictException extends BusinessException {

    public ReservationConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "RESERVATION_CONFLICT");
    }
}
