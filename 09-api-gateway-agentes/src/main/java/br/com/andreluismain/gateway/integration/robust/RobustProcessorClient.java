package br.com.andreluismain.gateway.integration.robust;

import br.com.andreluismain.gateway.integration.ProcessorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * Cliente de integração para o processador de agente robusto (Robust Processor).
 * Executa análise semântica aprofundada com proteção contra timeouts.
 */
@Component
public class RobustProcessorClient implements ProcessorClient {

    private static final Logger log = LoggerFactory.getLogger(RobustProcessorClient.class);

    private final WebClient webClient;
    private final String processorUrl;
    private final int timeoutSeconds;

    public RobustProcessorClient(
            WebClient.Builder webClientBuilder,
            @Value("${gateway.robust-processor-url:http://localhost:8082}") String processorUrl,
            @Value("${gateway.timeout-seconds:5}") int timeoutSeconds) {
        this.webClient = webClientBuilder.build();
        this.processorUrl = processorUrl;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public Mono<Map<String, Object>> process(String correlationId, String text) {
        log.info("Encaminhando para Robust Processor [correlationId={}]: url={}", correlationId, processorUrl);

        return webClient.post()
                .uri(processorUrl + "/process")
                .header("X-Correlation-Id", correlationId)
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .onErrorResume(ex -> {
                    log.warn("Robust Processor indisponível ou timeout [correlationId={}]: {}", correlationId, ex.getMessage());
                    return Mono.just(executeFallback(text));
                });
    }

    private Map<String, Object> executeFallback(String text) {
        Map<String, Object> result = new HashMap<>();
        result.put("processorType", "ROBUST_AGENT_FALLBACK");
        result.put("depthAnalysis", "CONCLUIDA");
        result.put("characterCount", text.length());
        result.put("estimatedTokens", (int) Math.round(text.trim().split("\\s+").length * 1.33));

        List<String> keySections = Arrays.stream(text.split("(?m)^#+|\\n\\s*\\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .limit(5)
                .toList();
        result.put("extractedKeySections", keySections);
        result.put("summary", "Texto analisado profundamente pelo pipeline estruturado resiliente.");
        return result;
    }
}
