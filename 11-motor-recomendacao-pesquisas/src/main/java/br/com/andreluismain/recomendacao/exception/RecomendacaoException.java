package br.com.andreluismain.recomendacao.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção base de domínio para o motor de recomendações de pesquisas.
 */
public class RecomendacaoException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public RecomendacaoException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
