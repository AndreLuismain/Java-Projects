package br.com.andreluismain.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando a cota de requisições por segundo é excedida pelo cliente.
 */
public class RateLimitExceededException extends GatewayException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super("Limite de requisições excedido. Tente novamente em " + retryAfterSeconds + " segundos.",
                HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
