package br.com.andreluismain.recomendacao.etl.extract;

import java.util.List;

/**
 * Representação intermediária dos dados de uma oportunidade extraída a partir de múltiplos aliases.
 */
public record RawOpportunity(
        String sourceId,
        String title,
        String institution,
        String description,
        List<String> rawTechnologies,
        List<String> rawAreas,
        String modality,
        String rawDeadline
) {}
