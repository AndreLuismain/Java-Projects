package br.com.andreluismain.gateway.routing;

import br.com.andreluismain.gateway.dto.ProcessingMode;
import br.com.andreluismain.gateway.dto.RouteType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Avaliador determinístico de complexidade textual para roteamento de agentes.
 * Mede volume, tokens aproximados, quebras estruturais e palavras-chave técnicas.
 */
@Component
public class ComplexityEvaluator {

    private static final List<String> DOMAIN_KEYWORDS = Arrays.asList(
            "arquitetura", "pesquisa", "análise profunda", "otimização", "algoritmo",
            "machine learning", "deep learning", "segurança", "concorrência", "complexo",
            "benchmark", "distribuído", "invariante", "idempotência", "transação"
    );

    @Value("${gateway.complexity-threshold:50}")
    private int complexityThreshold = 50;

    public ComplexityEvaluator() {
    }

    public ComplexityEvaluator(int complexityThreshold) {
        this.complexityThreshold = complexityThreshold;
    }

    /**
     * Calcula o score de complexidade do texto (0 a 100) e determina a rota apropriada.
     */
    public RouteDecision evaluate(String text, ProcessingMode mode) {
        int score = calculateComplexityScore(text);

        ProcessingMode effectiveMode = (mode != null) ? mode : ProcessingMode.AUTO;
        RouteType chosenRoute;
        String explanation;

        switch (effectiveMode) {
            case FAST -> {
                chosenRoute = RouteType.FAST;
                explanation = "Roteamento manual forçado para FAST.";
            }
            case ROBUST -> {
                chosenRoute = RouteType.ROBUST;
                explanation = "Roteamento manual forçado para ROBUST.";
            }
            case AUTO -> {
                if (score >= complexityThreshold) {
                    chosenRoute = RouteType.ROBUST;
                    explanation = String.format("Score de complexidade (%d) atingiu ou superou o limiar (%d).",
                            score, complexityThreshold);
                } else {
                    chosenRoute = RouteType.FAST;
                    explanation = String.format("Score de complexidade (%d) abaixo do limiar (%d).",
                            score, complexityThreshold);
                }
            }
            default -> {
                chosenRoute = RouteType.FAST;
                explanation = "Modo padrão aplicado: FAST.";
            }
        }

        return new RouteDecision(chosenRoute, score, explanation);
    }

    /**
     * Calcula uma pontuação ponderada determinística de complexidade entre 0 e 100.
     */
    public int calculateComplexityScore(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        int charLength = text.length();
        String[] words = text.trim().split("\\s+");
        int wordCount = words.length;

        // 1. Pontos por extensão em caracteres (máximo 40 pontos para 2000+ chars)
        int lengthScore = Math.min(40, (charLength * 40) / 2000);

        // 2. Pontos por quantidade de seções estruturadas (cabeçalhos '#' ou parágrafos duplos)
        int sectionCount = text.split("(?m)^#+|\\n\\s*\\n").length;
        int sectionScore = Math.min(20, (sectionCount - 1) * 5);

        // 3. Pontos por palavras-chave do domínio técnico
        String lowerText = text.toLowerCase(Locale.ROOT);
        long keywordCount = DOMAIN_KEYWORDS.stream()
                .filter(lowerText::contains)
                .count();
        int keywordScore = Math.min(40, (int) (keywordCount * 10));

        int total = lengthScore + sectionScore + keywordScore;
        return Math.min(100, Math.max(0, total));
    }

    public int getComplexityThreshold() {
        return complexityThreshold;
    }

    public void setComplexityThreshold(int complexityThreshold) {
        this.complexityThreshold = complexityThreshold;
    }
}
