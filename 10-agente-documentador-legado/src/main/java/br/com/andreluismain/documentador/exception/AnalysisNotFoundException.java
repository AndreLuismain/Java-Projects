package br.com.andreluismain.documentador.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exceção lançada quando a análise solicitada não é localizada.
 */
public class AnalysisNotFoundException extends DocumentadorException {

    public AnalysisNotFoundException(UUID id) {
        super("Análise com id '" + id + "' não encontrada.", HttpStatus.NOT_FOUND, "ANALYSIS_NOT_FOUND");
    }
}
