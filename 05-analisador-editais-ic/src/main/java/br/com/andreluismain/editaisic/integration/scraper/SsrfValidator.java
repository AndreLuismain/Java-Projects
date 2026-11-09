package br.com.andreluismain.editaisic.integration.scraper;

import br.com.andreluismain.editaisic.exception.SsrfSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Set;

/**
 * Validador de segurança contra ataques de Server-Side Request Forgery (SSRF).
 *
 * <p>Impede que requisições de coleta façam chamadas para a rede interna,
 * endereços loopback, localhost, faixas privadas (RFC 1918) ou esquemas não-web.</p>
 */
@Component
public class SsrfValidator {

    private static final Logger log = LoggerFactory.getLogger(SsrfValidator.class);

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final Set<String> BLOCKED_HOSTS = Set.of("localhost", "127.0.0.1", "0.0.0.0", "::1", "metadata.google.internal");

    /**
     * Valida uma URL contra ataques SSRF antes de realizar a requisição HTTP.
     *
     * @param urlString URL a ser inspecionada
     * @throws SsrfSecurityException se a URL for perigosa ou apontar para a rede interna
     */
    public void validateUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new SsrfSecurityException("A URL não pode estar vazia.");
        }

        URI uri;
        try {
            uri = URI.create(urlString.trim());
        } catch (IllegalArgumentException e) {
            throw new SsrfSecurityException("URL com formato malformado: " + e.getMessage());
        }

        String scheme = uri.getScheme();
        if (scheme == null || !ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))) {
            throw new SsrfSecurityException("Esquema de URL não permitido: '" + scheme + "'. Apenas HTTP e HTTPS são aceitos.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new SsrfSecurityException("A URL não possui um host válido.");
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);
        if (BLOCKED_HOSTS.contains(normalizedHost) || normalizedHost.endsWith(".local") || normalizedHost.endsWith(".internal")) {
            throw new SsrfSecurityException("Acesso bloqueado por segurança: o host '" + host + "' é restrito.");
        }

        // Resolução DNS para inspecionar os endereços IP reais do host
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (address.isAnyLocalAddress()
                        || address.isLoopbackAddress()
                        || address.isLinkLocalAddress()
                        || address.isSiteLocalAddress()
                        || address.isMulticastAddress()) {
                    log.warn("Tentativa de SSRF detectada: host '{}' resolveu para endereço privado '{}'", host, address.getHostAddress());
                    throw new SsrfSecurityException("Acesso bloqueado: o host resolve para um endereço IP privado ou interno.");
                }
            }
        } catch (UnknownHostException e) {
            throw new SsrfSecurityException("Não foi possível resolver o endereço do host: " + host);
        }
    }
}
