package br.com.andreluismain.agendamento.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando o recurso solicitado não é encontrado.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(UUID id) {
        super("Recurso acadêmico com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
