package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando um recurso solicitado (perfil, oportunidade, análise ou job) não é encontrado.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
