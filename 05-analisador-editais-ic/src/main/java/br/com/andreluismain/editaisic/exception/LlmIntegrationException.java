package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando a integração com a API do Gemini falha de forma irrecuperável
 * ou quando a resposta não segue o contrato JSON esperado.
 */
public class LlmIntegrationException extends BusinessException {

    public LlmIntegrationException(String message) {
        super(message, HttpStatus.BAD_GATEWAY, "LLM_INTEGRATION_ERROR");
    }

    public LlmIntegrationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_GATEWAY, "LLM_INTEGRATION_ERROR");
    }
}
