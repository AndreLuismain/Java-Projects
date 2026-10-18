package br.com.andreluismain.recomendacao.etl.extract;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Extrator que mapeia payloads heterogêneos com aliases variados para o modelo intermediário comum.
 */
@Component
public class OpportunityExtractor {

    private static final List<String> TITLE_ALIASES = List.of("title", "titulo", "vaga", "nome", "name");
    private static final List<String> INSTITUTION_ALIASES = List.of("institution", "instituicao", "universidade", "empresa", "faculdade");
    private static final List<String> DESCRIPTION_ALIASES = List.of("description", "descricao", "resumo", "detalhes", "summary");
    private static final List<String> TECH_ALIASES = List.of("technologies", "tecnologias", "techs", "skills", "linguagens");
    private static final List<String> AREA_ALIASES = List.of("researchAreas", "areas", "areasDePesquisa", "area", "campo");
    private static final List<String> MODALITY_ALIASES = List.of("modality", "modalidade", "tipo", "formato");
    private static final List<String> DEADLINE_ALIASES = List.of("deadline", "prazo", "dataLimite", "data_limite", "validade");

    /**
     * Extrai oportunidade a partir de mapa arbitrário aplicando resolução de aliases.
     */
    @SuppressWarnings("unchecked")
    public RawOpportunity extractFromMap(Map<String, Object> map) {
        String sourceId = extractString(map, List.of("id", "sourceId", "codigo"));
        String title = extractString(map, TITLE_ALIASES);
        String institution = extractString(map, INSTITUTION_ALIASES);
        String description = extractString(map, DESCRIPTION_ALIASES);
        List<String> techs = extractList(map, TECH_ALIASES);
        List<String> areas = extractList(map, AREA_ALIASES);
        String modality = extractString(map, MODALITY_ALIASES);
        String deadline = extractString(map, DEADLINE_ALIASES);

        return new RawOpportunity(sourceId, title, institution, description, techs, areas, modality, deadline);
    }

    private String extractString(Map<String, Object> map, List<String> aliases) {
        for (String alias : aliases) {
            Object val = map.get(alias);
            if (val != null) {
                return val.toString().trim();
            }
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private List<String> extractList(Map<String, Object> map, List<String> aliases) {
        for (String alias : aliases) {
            Object val = map.get(alias);
            if (val instanceof List<?> list) {
                return list.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .toList();
            } else if (val instanceof String str) {
                return Arrays.stream(str.split("[,;]"))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
