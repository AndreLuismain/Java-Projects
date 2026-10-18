package br.com.andreluismain.gateway.rate_limit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {

    private TokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new TokenBucketRateLimiter(3, 1.0); // Capacidade 3, 1 token/s
    }

    @Test
    @DisplayName("Deve permitir requisições até a capacidade limite do balde")
    void shouldAllowRequestsUpToCapacity() {
        String client = "client-test-1";

        assertTrue(rateLimiter.tryAcquire(client, 1));
        assertTrue(rateLimiter.tryAcquire(client, 1));
        assertTrue(rateLimiter.tryAcquire(client, 1));

        // 4ª requisição imediata deve ser bloqueada
        assertFalse(rateLimiter.tryAcquire(client, 1));
    }

    @Test
    @DisplayName("Deve calcular Retry-After quando taxa for excedida")
    void shouldCalculateRetryAfterWhenExceeded() {
        String client = "client-test-2";

        rateLimiter.tryAcquire(client, 3);
        assertFalse(rateLimiter.tryAcquire(client, 1));

        long retryAfter = rateLimiter.getRetryAfterSeconds(client, 1);
        assertTrue(retryAfter >= 1);
    }
}
