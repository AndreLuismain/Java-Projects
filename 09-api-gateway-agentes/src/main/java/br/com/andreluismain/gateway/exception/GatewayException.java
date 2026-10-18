package br.com.andreluismain.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base para cenários de erro do gateway de agentes.
 */
public class GatewayException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public GatewayException(String message, HttpStatus status, String code) {
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
