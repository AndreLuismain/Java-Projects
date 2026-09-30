package br.com.andreluismain.pix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base de domínio para o simulador Pix.
 */
public class PixException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public PixException(String message, HttpStatus status, String code) {
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
