package br.com.andreluismain.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando o corpo da requisição viola políticas de tamanho ou formato do gateway.
 */
public class InvalidPayloadException extends GatewayException {

    public InvalidPayloadException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_PAYLOAD");
    }
}
