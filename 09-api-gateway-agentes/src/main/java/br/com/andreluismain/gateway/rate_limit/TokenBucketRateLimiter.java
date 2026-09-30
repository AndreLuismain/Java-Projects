package br.com.andreluismain.gateway.rate_limit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Limitador de taxa de requisições baseado no algoritmo Token Bucket em memória.
 * Oferece controle por chave de cliente com cálculo determinístico de Retry-After.
 */
@Component
public class TokenBucketRateLimiter {

    @Value("${gateway.rate-limit-capacity:10}")
    private int defaultCapacity = 10;

    @Value("${gateway.rate-limit-refill-per-second:2}")
    private double defaultRefillRate = 2.0;

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter() {
    }

    public TokenBucketRateLimiter(int defaultCapacity, double defaultRefillRate) {
        this.defaultCapacity = defaultCapacity;
        this.defaultRefillRate = defaultRefillRate;
    }

    /**
     * Tenta consumir tokens para um determinado cliente.
     *
     * @param clientId identificador ou IP do cliente
     * @param tokens quantidade de tokens a consumir
     * @return true se permitido, false se a taxa foi excedida
     */
    public boolean tryAcquire(String clientId, int tokens) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(defaultCapacity, defaultRefillRate));
        return bucket.tryConsume(tokens);
    }

    /**
     * Retorna a quantidade estimada de tokens restantes para o cliente.
     */
    public int getAvailableTokens(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(defaultCapacity, defaultRefillRate));
        return bucket.getAvailableTokens();
    }

    /**
     * Retorna o tempo em segundos que o cliente deve aguardar antes de tentar novamente.
     */
    public long getRetryAfterSeconds(String clientId, int tokens) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(defaultCapacity, defaultRefillRate));
        return bucket.getWaitSeconds(tokens);
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public double getDefaultRefillRate() {
        return defaultRefillRate;
    }

    /**
     * Balde de tokens individual thread-safe.
     */
    private static class TokenBucket {
        private final int capacity;
        private final double refillRatePerSecond;
        private double tokens;
        private long lastRefillNanos;

        public TokenBucket(int capacity, double refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        public synchronized boolean tryConsume(int requiredTokens) {
            refill();
            if (tokens >= requiredTokens) {
                tokens -= requiredTokens;
                return true;
            }
            return false;
        }

        public synchronized int getAvailableTokens() {
            refill();
            return (int) Math.floor(tokens);
        }

        public synchronized long getWaitSeconds(int requiredTokens) {
            refill();
            if (tokens >= requiredTokens) {
                return 0;
            }
            double needed = requiredTokens - tokens;
            return Math.max(1, (long) Math.ceil(needed / refillRatePerSecond));
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
            if (elapsedSeconds > 0) {
                tokens = Math.min(capacity, tokens + (elapsedSeconds * refillRatePerSecond));
                lastRefillNanos = now;
            }
        }
    }
}
