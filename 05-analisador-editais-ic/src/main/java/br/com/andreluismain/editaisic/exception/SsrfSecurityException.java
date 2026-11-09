package br.com.andreluismain.editaisic.exception;

import org.springframework.http.HttpStatus;

/**
 * Lançada quando uma URL submetida viola regras de segurança contra Server-Side Request Forgery (SSRF),
 * como apontar para localhost, faixas de IP privadas ou esquemas não permitidos.
 */
public class SsrfSecurityException extends BusinessException {

    public SsrfSecurityException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "SSRF_BLOCKED");
    }
}
