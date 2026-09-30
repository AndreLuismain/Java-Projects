package br.com.andreluismain.recomendacao.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando a execução de recomendação solicitada não é localizada.
 */
public class RecommendationRunNotFoundException extends RecomendacaoException {

    public RecommendationRunNotFoundException(UUID id) {
        super("Execução de recomendação com id '" + id + "' não encontrada.", HttpStatus.NOT_FOUND, "RUN_NOT_FOUND");
    }
}
