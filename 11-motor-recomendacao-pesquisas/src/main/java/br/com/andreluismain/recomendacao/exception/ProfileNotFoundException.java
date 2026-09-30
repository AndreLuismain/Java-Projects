package br.com.andreluismain.recomendacao.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando o perfil do estudante não é localizado.
 */
public class ProfileNotFoundException extends RecomendacaoException {

    public ProfileNotFoundException(UUID id) {
        super("Perfil com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND");
    }
}
