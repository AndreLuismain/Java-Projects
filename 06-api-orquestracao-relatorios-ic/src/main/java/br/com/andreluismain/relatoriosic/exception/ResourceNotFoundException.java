package br.com.andreluismain.relatoriosic.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando o recurso do projeto ou log não é localizado.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(UUID id) {
        super("Recurso com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
