package br.com.andreluismain.editaisic.integration.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de controle de vazão (Rate Limiting) por domínio para evitar sobrecarga em servidores externos.
 */
@Service
public class RateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);

    private final ConcurrentHashMap<String, Long> lastAccessPerDomain = new ConcurrentHashMap<>();

    @Value("${app.scraper.rate-limit-ms:1000}")
    private long rateLimitMs;

    /**
     * Aplica o delay necessário para respeitar o intervalo mínimo de requisição para o domínio da URL.
     *
     * @param urlString URL de destino
     */
    public void throttle(String urlString) {
        try {
            URI uri = URI.create(urlString);
            String domain = uri.getHost();
            if (domain == null) {
                return;
            }

            long now = System.currentTimeMillis();
            Long lastAccess = lastAccessPerDomain.get(domain);

            if (lastAccess != null) {
                long elapsed = now - lastAccess;
                if (elapsed < rateLimitMs) {
                    long waitTime = rateLimitMs - elapsed;
                    log.debug("Aplicando rate limit de {}ms para o domínio '{}'", waitTime, domain);
                    Thread.sleep(waitTime);
                }
            }
            lastAccessPerDomain.put(domain, System.currentTimeMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Rate limiting interrompido para {}", urlString);
        } catch (Exception e) {
            log.debug("Não foi possível aplicar rate limit para {}: {}", urlString, e.getMessage());
        }
    }
}
