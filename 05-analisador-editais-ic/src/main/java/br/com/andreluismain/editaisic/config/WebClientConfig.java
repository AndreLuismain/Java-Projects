package br.com.andreluismain.editaisic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuração de instâncias de WebClient para chamadas HTTP assíncronas e resilientes.
 */
@Configuration
public class WebClientConfig {

    @Value("${app.scraper.connect-timeout-ms:10000}")
    private int scraperConnectTimeout;

    @Value("${app.scraper.read-timeout-ms:15000}")
    private int scraperReadTimeout;

    @Value("${app.scraper.max-bytes:5242880}")
    private int maxBytes;

    @Value("${app.gemini.connect-timeout-ms:10000}")
    private int geminiConnectTimeout;

    @Value("${app.gemini.read-timeout-ms:30000}")
    private int geminiReadTimeout;

    /**
     * WebClient dedicado para o módulo de Web Scraping, com limites estritos de buffer e timeout.
     */
    @Bean(name = "scraperWebClient")
    public WebClient scraperWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(scraperReadTimeout))
                .followRedirect(true);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxBytes))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * WebClient dedicado para integração com a API do Google Gemini.
     */
    @Bean(name = "geminiWebClient")
    public WebClient geminiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(geminiReadTimeout));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }
}
