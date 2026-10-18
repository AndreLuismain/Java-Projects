package br.com.andreluismain.documentador.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada ao requisitar um documento técnico antes da conclusão da análise.
 */
public class DocumentNotReadyException extends DocumentadorException {

    public DocumentNotReadyException(String message) {
        super(message, HttpStatus.CONFLICT, "DOCUMENT_NOT_READY");
    }
}
