package br.com.andreluismain.relatoriosic.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base de domínio para a API de relatórios de IC.
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
