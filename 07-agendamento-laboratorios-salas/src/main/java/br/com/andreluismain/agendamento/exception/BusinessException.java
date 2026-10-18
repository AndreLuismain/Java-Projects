package br.com.andreluismain.agendamento.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base de negócio para o microserviço de agendamento.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
