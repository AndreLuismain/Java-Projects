package br.com.andreluismain.editaisic.integration.scraper;

import br.com.andreluismain.editaisic.exception.ScrapingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Serviço responsável pela coleta resiliente e segura de páginas de editais via HTTP.
 */
@Service
public class ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private final WebClient webClient;
    private final SsrfValidator ssrfValidator;
    private final HtmlExtractor htmlExtractor;
    private final RateLimiterService rateLimiterService;

    @Value("${app.scraper.read-timeout-ms:15000}")
    private int readTimeoutMs;

    @Value("${app.scraper.user-agent:Mozilla/5.0 (compatible; AnalisadorEditaisIC/1.0)}")
    private String userAgent;

    public ScraperService(
            @Qualifier("scraperWebClient") WebClient webClient,
            SsrfValidator ssrfValidator,
            HtmlExtractor htmlExtractor,
            RateLimiterService rateLimiterService
    ) {
        this.webClient = webClient;
        this.ssrfValidator = ssrfValidator;
        this.htmlExtractor = htmlExtractor;
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * Coleta a página da URL indicada, validando regras de SSRF, respeitando rate limits e
     * extraindo o texto e metadados de forma normalizada.
     *
     * @param url URL da oportunidade a ser coletada
     * @return {@link ExtractedContent} normalizado com hash de conteúdo
     */
    public ExtractedContent fetchAndExtract(String url) {
        log.info("Iniciando coleta de edital na URL: {}", url);

        // 1. Validação de segurança contra SSRF
        ssrfValidator.validateUrl(url);

        // 2. Aplicação de Rate Limiter para o domínio
        rateLimiterService.throttle(url);

        // 3. Chamada HTTP com limites de timeout e tamanho
        String html;
        try {
            html = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(readTimeoutMs))
                    .block(); // Bloqueia dentro da thread assíncrona do pool
        } catch (Exception e) {
            log.error("Falha ao coletar página na URL {}: {}", url, e.getMessage());
            throw new ScrapingException("Falha ao conectar ou ler o conteúdo da página: " + e.getMessage(), e);
        }

        if (html == null || html.isBlank()) {
            throw new ScrapingException("A resposta obtida da URL está vazia ou sem conteúdo.");
        }

        // 4. Extração e sanitização do HTML
        return htmlExtractor.extract(html, url);
    }
}
