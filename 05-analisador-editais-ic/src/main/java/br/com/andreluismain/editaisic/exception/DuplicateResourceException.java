package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando há tentativa de cadastrar um recurso que viola restrição de unicidade (ex: URL duplicada).
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}
