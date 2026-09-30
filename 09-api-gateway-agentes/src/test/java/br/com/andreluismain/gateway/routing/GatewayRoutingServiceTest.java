package br.com.andreluismain.gateway.routing;

import br.com.andreluismain.gateway.dto.ProcessRequest;
import br.com.andreluismain.gateway.dto.ProcessResponse;
import br.com.andreluismain.gateway.dto.ProcessingMode;
import br.com.andreluismain.gateway.dto.RouteType;
import br.com.andreluismain.gateway.exception.InvalidPayloadException;
import br.com.andreluismain.gateway.exception.RateLimitExceededException;
import br.com.andreluismain.gateway.integration.fast.FastProcessorClient;
import br.com.andreluismain.gateway.integration.robust.RobustProcessorClient;
import br.com.andreluismain.gateway.rate_limit.TokenBucketRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayRoutingServiceTest {

    @Mock
    private ComplexityEvaluator complexityEvaluator;

    @Mock
    private TokenBucketRateLimiter rateLimiter;

    @Mock
    private FastProcessorClient fastProcessorClient;

    @Mock
    private RobustProcessorClient robustProcessorClient;

    private GatewayRoutingService routingService;

    @BeforeEach
    void setUp() {
        routingService = new GatewayRoutingService(
                complexityEvaluator,
                rateLimiter,
                fastProcessorClient,
                robustProcessorClient
        );
        routingService.setMinTextLength(5);
        routingService.setMaxTextLength(1000);
    }

    @Test
    @DisplayName("Deve rejeitar payload menor que o tamanho mínimo sem acionar rate limit nem clientes")
    void shouldRejectPayloadBelowMinLength() {
        ProcessRequest request = new ProcessRequest(UUID.randomUUID(), "oi", ProcessingMode.AUTO);

        Mono<ProcessResponse> mono = routingService.routeAndProcess(request, "client-1", "corr-1");

        StepVerifier.create(mono)
                .expectError(InvalidPayloadException.class)
                .verify();

        verifyNoInteractions(rateLimiter);
        verifyNoInteractions(fastProcessorClient);
        verifyNoInteractions(robustProcessorClient);
    }

    @Test
    @DisplayName("Deve rejeitar requisição com 429 quando rate limit for excedido sem acionar downstream")
    void shouldRejectWhenRateLimitExceeded() {
        ProcessRequest request = new ProcessRequest(UUID.randomUUID(), "Texto de tamanho valido para processamento", ProcessingMode.AUTO);

        when(rateLimiter.tryAcquire(anyString(), anyInt())).thenReturn(false);
        when(rateLimiter.getRetryAfterSeconds(anyString(), anyInt())).thenReturn(5L);

        Mono<ProcessResponse> mono = routingService.routeAndProcess(request, "client-rate", "corr-2");

        StepVerifier.create(mono)
                .expectError(RateLimitExceededException.class)
                .verify();

        verifyNoInteractions(complexityEvaluator);
        verifyNoInteractions(fastProcessorClient);
        verifyNoInteractions(robustProcessorClient);
    }

    @Test
    @DisplayName("Deve encaminhar com sucesso para rota FAST quando selecionada")
    void shouldRouteToFastSuccessfully() {
        String text = "Texto valido de exemplo para teste do gateway";
        ProcessRequest request = new ProcessRequest(UUID.randomUUID(), text, ProcessingMode.FAST);

        when(rateLimiter.tryAcquire("client-ok", 1)).thenReturn(true);
        when(complexityEvaluator.evaluate(text, ProcessingMode.FAST))
                .thenReturn(new RouteDecision(RouteType.FAST, 15, "Baixa complexidade"));
        when(fastProcessorClient.process(eq("corr-ok"), eq(text)))
                .thenReturn(Mono.just(Map.of("status", "fast_processed")));

        Mono<ProcessResponse> mono = routingService.routeAndProcess(request, "client-ok", "corr-ok");

        StepVerifier.create(mono)
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(RouteType.FAST, response.route());
                    org.junit.jupiter.api.Assertions.assertEquals("COMPLETED", response.status());
                })
                .verifyComplete();

        verify(fastProcessorClient).process("corr-ok", text);
        verifyNoInteractions(robustProcessorClient);
    }
}
