package br.com.andreluismain.documentador.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base para o serviço documentador de código legado.
 */
public class DocumentadorException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public DocumentadorException(String message, HttpStatus status, String code) {
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
