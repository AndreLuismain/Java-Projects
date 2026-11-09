package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando ocorre uma falha na coleta HTTP de uma oportunidade externa
 * (ex: timeout de leitura, HTTP 404/500 no site de origem, tamanho excedido).
 */
public class ScrapingException extends BusinessException {

    public ScrapingException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "SCRAPING_FAILED");
    }

    public ScrapingException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNPROCESSABLE_ENTITY, "SCRAPING_FAILED");
    }
}
