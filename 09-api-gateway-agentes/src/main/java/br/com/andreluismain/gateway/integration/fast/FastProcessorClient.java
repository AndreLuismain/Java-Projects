package br.com.andreluismain.gateway.integration.fast;

import br.com.andreluismain.gateway.integration.ProcessorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cliente de integração para o processador rápido (Fast Processor).
 * Utiliza WebClient com timeout configurado e fallback heurístico resiliente.
 */
@Component
public class FastProcessorClient implements ProcessorClient {

    private static final Logger log = LoggerFactory.getLogger(FastProcessorClient.class);

    private final WebClient webClient;
    private final String processorUrl;
    private final int timeoutSeconds;

    public FastProcessorClient(
            WebClient.Builder webClientBuilder,
            @Value("${gateway.fast-processor-url:http://localhost:8081}") String processorUrl,
            @Value("${gateway.timeout-seconds:5}") int timeoutSeconds) {
        this.webClient = webClientBuilder.build();
        this.processorUrl = processorUrl;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public Mono<Map<String, Object>> process(String correlationId, String text) {
        log.info("Encaminhando para Fast Processor [correlationId={}]: url={}", correlationId, processorUrl);

        return webClient.post()
                .uri(processorUrl + "/process")
                .header("X-Correlation-Id", correlationId)
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .onErrorResume(ex -> {
                    log.warn("Fast Processor indisponível ou timeout [correlationId={}]: {}", correlationId, ex.getMessage());
                    return Mono.just(executeFallback(text));
                });
    }

    private Map<String, Object> executeFallback(String text) {
        Map<String, Object> result = new HashMap<>();
        result.put("processorType", "FAST_HEURISTIC_FALLBACK");
        result.put("characterCount", text.length());
        result.put("wordCount", text.trim().split("\\s+").length);
        result.put("preview", text.length() > 60 ? text.substring(0, 60) + "..." : text);
        result.put("note", "Processamento rápido executado via motor heurístico embarcado");
        return result;
    }
}
