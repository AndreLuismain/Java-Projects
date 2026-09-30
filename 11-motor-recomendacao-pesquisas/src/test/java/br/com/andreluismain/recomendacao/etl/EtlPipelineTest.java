package br.com.andreluismain.recomendacao.etl;

import br.com.andreluismain.recomendacao.etl.extract.OpportunityExtractor;
import br.com.andreluismain.recomendacao.etl.extract.RawOpportunity;
import br.com.andreluismain.recomendacao.etl.transform.OpportunityDeduplicator;
import br.com.andreluismain.recomendacao.etl.transform.OpportunityTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EtlPipelineTest {

    private final OpportunityExtractor extractor = new OpportunityExtractor();
    private final OpportunityTransformer transformer = new OpportunityTransformer();
    private final OpportunityDeduplicator deduplicator = new OpportunityDeduplicator();

    @Test
    @DisplayName("Deve extrair e normalizar campos utilizando múltiplos aliases equivalentes")
    void shouldExtractAndNormalizeUsingAliases() {
        Map<String, Object> rawMap = Map.of(
                "vaga", "Pesquisa em IA e Visão Computacional",
                "instituicao", "USP",
                "resumo", "Desenvolvimento de modelos neurais profundos.",
                "techs", List.of("Java 21", "Python 3", "Spring Boot"),
                "areas", List.of("Inteligência Artificial"),
                "modalidade", "Online",
                "prazo", "2026-12-31"
        );

        RawOpportunity raw = extractor.extractFromMap(rawMap);
        assertNotNull(raw);
        assertEquals("Pesquisa em IA e Visão Computacional", raw.title());

        OpportunityTransformer.TransformedOpportunity transformed = transformer.transform(raw);

        assertEquals("REMOTO", transformed.modality());
        assertTrue(transformed.normalizedTechnologies().contains("java"));
        assertTrue(transformed.normalizedTechnologies().contains("python"));
        assertTrue(transformed.normalizedTechnologies().contains("spring"));
        assertTrue(transformed.normalizedAreas().contains("ia"));
        assertEquals(LocalDate.of(2026, 12, 31), transformed.deadline());
    }

    @Test
    @DisplayName("Deve deduplicar oportunidades idênticas gerando lista com elementos únicos")
    void shouldDeduplicateIdenticalOpportunities() {
        OpportunityTransformer.TransformedOpportunity opp1 = new OpportunityTransformer.TransformedOpportunity(
                "1", "Iniciação Científica em Robótica", "UFMG", "Pesquisa em automação",
                List.of("c++", "ros"), List.of("robotica"), "PRESENCIAL", LocalDate.of(2026, 10, 1)
        );

        OpportunityTransformer.TransformedOpportunity opp2 = new OpportunityTransformer.TransformedOpportunity(
                "2", "Iniciação Científica em Robótica", "UFMG", "Pesquisa em automação",
                List.of("c++", "ros"), List.of("robotica"), "PRESENCIAL", LocalDate.of(2026, 10, 1)
        );

        OpportunityTransformer.TransformedOpportunity opp3 = new OpportunityTransformer.TransformedOpportunity(
                "3", "Outra Oportunidade Diferente", "UNICAMP", "Engenharia de Software",
                List.of("java"), List.of("engenharia"), "REMOTO", LocalDate.of(2026, 11, 15)
        );

        List<OpportunityDeduplicator.DeduplicatedItem> unique = deduplicator.deduplicate(List.of(opp1, opp2, opp3));

        assertEquals(2, unique.size());
        assertEquals("Iniciação Científica em Robótica", unique.get(0).opportunity().title());
        assertEquals("Outra Oportunidade Diferente", unique.get(1).opportunity().title());
    }
}
