package br.com.andreluismain.gateway.routing;

import br.com.andreluismain.gateway.dto.ProcessingMode;
import br.com.andreluismain.gateway.dto.RouteType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComplexityEvaluatorTest {

    private ComplexityEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ComplexityEvaluator(50);
    }

    @Test
    @DisplayName("Texto simples e curto deve ser classificado com baixo score e direcionado para FAST")
    void shouldRouteSimpleTextToFast() {
        String text = "Olá, preciso de um resumo rápido deste pequeno parágrafo.";
        RouteDecision decision = evaluator.evaluate(text, ProcessingMode.AUTO);

        assertNotNull(decision);
        assertEquals(RouteType.FAST, decision.route());
        assertTrue(decision.complexityScore() < 50);
    }

    @Test
    @DisplayName("Texto longo com termos técnicos e seções deve atingir limiar e ir para ROBUST")
    void shouldRouteComplexTechnicalTextToRobust() {
        String text = """
                # Arquitetura de Redes Neurais e Machine Learning
                
                Este artigo aborda a pesquisa profunda sobre otimização de modelo e algoritmo distribuído.
                
                ## Análise Profunda e Benchmark
                
                A concorrência em sistemas distribuídos exige garantias de idempotência e transação com invariante formal.
                Avaliamos o impacto de deep learning e análise profunda em cenários de alta complexidade.
                """;

        RouteDecision decision = evaluator.evaluate(text, ProcessingMode.AUTO);

        assertNotNull(decision);
        assertEquals(RouteType.ROBUST, decision.route());
        assertTrue(decision.complexityScore() >= 50);
    }

    @Test
    @DisplayName("Modo FAST manual deve sobrepor a complexidade alta do texto")
    void shouldHonorManualFastMode() {
        String text = "Texto com termos de machine learning e arquitetura complexa distribuído.";
        RouteDecision decision = evaluator.evaluate(text, ProcessingMode.FAST);

        assertEquals(RouteType.FAST, decision.route());
    }

    @Test
    @DisplayName("Modo ROBUST manual deve sobrepor a baixa complexidade do texto")
    void shouldHonorManualRobustMode() {
        String text = "Texto simples e curto.";
        RouteDecision decision = evaluator.evaluate(text, ProcessingMode.ROBUST);

        assertEquals(RouteType.ROBUST, decision.route());
    }

    @Test
    @DisplayName("Mesmo texto deve produzir exatamente o mesmo score determinístico")
    void shouldBeDeterministicForSameInput() {
        String text = "Texto de teste com parâmetros estáveis.";
        int score1 = evaluator.calculateComplexityScore(text);
        int score2 = evaluator.calculateComplexityScore(text);

        assertEquals(score1, score2);
    }
}
