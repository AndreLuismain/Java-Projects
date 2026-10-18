package br.com.andreluismain.gateway.controller;

import br.com.andreluismain.gateway.dto.*;
import br.com.andreluismain.gateway.rate_limit.TokenBucketRateLimiter;
import br.com.andreluismain.gateway.routing.GatewayRoutingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Controller reativo REST para exposição do API Gateway de Agentes.
 */
@RestController
@RequestMapping("/api/v1")
public class ProcessController {

    private final GatewayRoutingService routingService;
    private final TokenBucketRateLimiter rateLimiter;

    public ProcessController(GatewayRoutingService routingService, TokenBucketRateLimiter rateLimiter) {
        this.routingService = routingService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Endpoint principal para recebimento, classificação e roteamento de texto para agentes.
     */
    @PostMapping("/process")
    public Mono<ResponseEntity<ProcessResponse>> process(
            @Valid @RequestBody ProcessRequest request,
            ServerWebExchange exchange) {

        String correlationId = exchange.getResponse().getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null) {
            correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        }
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        String clientId = extractClientId(exchange);

        return routingService.routeAndProcess(request, clientId, correlationId)
                .map(ResponseEntity::ok);
    }

    /**
     * Endpoint de verificação de disponibilidade e saúde do gateway.
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<HealthResponse>> health() {
        HealthResponse response = new HealthResponse(
                "UP",
                LocalDateTime.now(),
                Map.of(
                        "gateway", "OPERATIONAL",
                        "rateLimiter", "ACTIVE",
                        "routingEngine", "READY"
                )
        );
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Endpoint de consulta das cotas e limites do cliente solicitante.
     */
    @GetMapping("/limits")
    public Mono<ResponseEntity<LimitsResponse>> limits(ServerWebExchange exchange) {
        String clientId = extractClientId(exchange);
        int available = rateLimiter.getAvailableTokens(clientId);

        LimitsResponse response = new LimitsResponse(
                clientId,
                rateLimiter.getDefaultCapacity(),
                available,
                (int) rateLimiter.getDefaultRefillRate(),
                60
        );

        return Mono.just(ResponseEntity.ok(response));
    }

    private String extractClientId(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }
        String clientIdHeader = exchange.getRequest().getHeaders().getFirst("X-Client-Id");
        if (clientIdHeader != null && !clientIdHeader.isBlank()) {
            return clientIdHeader;
        }
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "default-client";
    }
}
