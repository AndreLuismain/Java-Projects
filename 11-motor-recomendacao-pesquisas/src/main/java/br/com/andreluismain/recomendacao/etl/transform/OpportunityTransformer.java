package br.com.andreluismain.recomendacao.etl.transform;

import br.com.andreluismain.recomendacao.etl.extract.RawOpportunity;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Transformador que normaliza campos, strings, tecnologias, modalidades e datas de oportunidades.
 */
@Component
public class OpportunityTransformer {

    private static final Map<String, String> CANONICAL_TECHS = Map.ofEntries(
            Map.entry("java 21", "java"),
            Map.entry("java 17", "java"),
            Map.entry("python3", "python"),
            Map.entry("python 3", "python"),
            Map.entry("js", "javascript"),
            Map.entry("ts", "typescript"),
            Map.entry("spring boot", "spring"),
            Map.entry("react.js", "react"),
            Map.entry("reactjs", "react"),
            Map.entry("vue.js", "vue"),
            Map.entry("vuejs", "vue"),
            Map.entry("node.js", "node"),
            Map.entry("nodejs", "node"),
            Map.entry("ia", "ia"),
            Map.entry("ai", "ia")
    );

    public record TransformedOpportunity(
            String sourceId,
            String title,
            String institution,
            String description,
            List<String> normalizedTechnologies,
            List<String> normalizedAreas,
            String modality,
            LocalDate deadline
    ) {}

    public TransformedOpportunity transform(RawOpportunity raw) {
        String title = cleanString(raw.title());
        String institution = cleanString(raw.institution());
        String description = cleanString(raw.description());

        List<String> techs = normalizeList(raw.rawTechnologies(), CANONICAL_TECHS);
        List<String> areas = normalizeList(raw.rawAreas(), Map.of("inteligência artificial", "ia", "inteligencia artificial", "ia"));

        String modality = resolveModality(raw.modality());
        LocalDate deadline = parseDate(raw.rawDeadline());

        return new TransformedOpportunity(
                raw.sourceId(),
                title,
                institution,
                description,
                techs,
                areas,
                modality,
                deadline
        );
    }

    private String cleanString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    private List<String> normalizeList(List<String> items, Map<String, String> synonyms) {
        if (items == null) {
            return Collections.emptyList();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String item : items) {
            if (item != null && !item.isBlank()) {
                String clean = stripAccents(item.trim().toLowerCase(Locale.ROOT));
                String mapped = synonyms.getOrDefault(clean, clean);
                normalized.add(mapped);
            }
        }
        return new ArrayList<>(normalized);
    }

    private String resolveModality(String modality) {
        if (modality == null || modality.isBlank()) {
            return "PRESENCIAL";
        }
        String clean = stripAccents(modality.trim().toUpperCase(Locale.ROOT));
        if (clean.contains("REMOT") || clean.contains("ONLINE") || clean.contains("EAD")) {
            return "REMOTO";
        }
        if (clean.contains("HIBRID")) {
            return "HIBRIDO";
        }
        return "PRESENCIAL";
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        String clean = dateStr.trim();
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        for (DateTimeFormatter dtf : formatters) {
            try {
                return LocalDate.parse(clean, dtf);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    public static String stripAccents(String s) {
        if (s == null) {
            return "";
        }
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
