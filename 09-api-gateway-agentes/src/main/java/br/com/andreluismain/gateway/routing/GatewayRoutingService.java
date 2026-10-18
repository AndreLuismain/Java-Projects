package br.com.andreluismain.gateway.routing;

import br.com.andreluismain.gateway.dto.ProcessRequest;
import br.com.andreluismain.gateway.dto.ProcessResponse;
import br.com.andreluismain.gateway.dto.RouteType;
import br.com.andreluismain.gateway.exception.InvalidPayloadException;
import br.com.andreluismain.gateway.exception.RateLimitExceededException;
import br.com.andreluismain.gateway.integration.fast.FastProcessorClient;
import br.com.andreluismain.gateway.integration.robust.RobustProcessorClient;
import br.com.andreluismain.gateway.rate_limit.TokenBucketRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Serviço orquestrador do API Gateway de agentes.
 * Valida o payload, aplica rate limit, avalia a complexidade do texto e despacha para o destino adequado.
 */
@Service
public class GatewayRoutingService {

    private static final Logger log = LoggerFactory.getLogger(GatewayRoutingService.class);

    private final ComplexityEvaluator complexityEvaluator;
    private final TokenBucketRateLimiter rateLimiter;
    private final FastProcessorClient fastProcessorClient;
    private final RobustProcessorClient robustProcessorClient;

    @Value("${gateway.min-text-length:5}")
    private int minTextLength = 5;

    @Value("${gateway.max-text-length:20000}")
    private int maxTextLength = 20000;

    public GatewayRoutingService(
            ComplexityEvaluator complexityEvaluator,
            TokenBucketRateLimiter rateLimiter,
            FastProcessorClient fastProcessorClient,
            RobustProcessorClient robustProcessorClient) {
        this.complexityEvaluator = complexityEvaluator;
        this.rateLimiter = rateLimiter;
        this.fastProcessorClient = fastProcessorClient;
        this.robustProcessorClient = robustProcessorClient;
    }

    public void setMinTextLength(int minTextLength) {
        this.minTextLength = minTextLength;
    }

    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    /**
     * Processa a requisição através do pipeline do gateway de forma não-bloqueante.
     */
    public Mono<ProcessResponse> routeAndProcess(ProcessRequest request, String clientId, String correlationId) {
        // 1. Validação estrita do payload antes do rate limit
        String text = request.text();
        if (text == null || text.trim().length() < minTextLength) {
            return Mono.error(new InvalidPayloadException("O texto deve conter no mínimo " + minTextLength + " caracteres."));
        }
        if (text.length() > maxTextLength) {
            return Mono.error(new InvalidPayloadException("O texto excede o limite máximo permitido de " + maxTextLength + " caracteres."));
        }

        // 2. Aplicação de Rate Limiting
        String effectiveClientId = (clientId != null && !clientId.isBlank()) ? clientId : "anonymous";
        if (!rateLimiter.tryAcquire(effectiveClientId, 1)) {
            long retryAfter = rateLimiter.getRetryAfterSeconds(effectiveClientId, 1);
            return Mono.error(new RateLimitExceededException(retryAfter));
        }

        // 3. Avaliação de complexidade e decisão de roteamento
        RouteDecision decision = complexityEvaluator.evaluate(text, request.getEffectiveMode());
        UUID requestId = (request.requestId() != null) ? request.requestId() : UUID.randomUUID();

        log.info("Decisão de roteamento [correlationId={}]: rota={}, score={}, razão='{}'",
                correlationId, decision.route(), decision.complexityScore(), decision.explanation());

        // 4. Encaminhamento resiliente para o destino selecionado
        Mono<java.util.Map<String, Object>> downstreamMono;
        if (decision.route() == RouteType.FAST) {
            downstreamMono = fastProcessorClient.process(correlationId, text);
        } else {
            downstreamMono = robustProcessorClient.process(correlationId, text);
        }

        return downstreamMono.map(result -> new ProcessResponse(
                requestId,
                correlationId,
                decision.route(),
                decision.complexityScore(),
                "COMPLETED",
                result
        ));
    }
}
