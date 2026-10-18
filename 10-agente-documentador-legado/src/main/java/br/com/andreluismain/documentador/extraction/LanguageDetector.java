package br.com.andreluismain.documentador.extraction;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Detector e normalizador de linguagens de programação com base na extensão e metadados.
 */
@Component
public class LanguageDetector {

    private static final Map<String, String> EXTENSION_TO_LANGUAGE = Map.ofEntries(
            Map.entry("cbl", "COBOL"),
            Map.entry("cob", "COBOL"),
            Map.entry("pas", "PASCAL"),
            Map.entry("c", "C"),
            Map.entry("cpp", "C++"),
            Map.entry("java", "JAVA"),
            Map.entry("py", "PYTHON"),
            Map.entry("js", "JAVASCRIPT"),
            Map.entry("sql", "SQL"),
            Map.entry("sh", "SHELL"),
            Map.entry("php", "PHP")
    );

    /**
     * Determina a linguagem normalizada a partir da entrada fornecida ou da extensão do arquivo.
     */
    public String resolveLanguage(String providedLanguage, String filename) {
        if (providedLanguage != null && !providedLanguage.isBlank()) {
            return providedLanguage.trim().toUpperCase();
        }

        if (filename != null) {
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex < filename.length() - 1) {
                String ext = filename.substring(dotIndex + 1).toLowerCase();
                if (EXTENSION_TO_LANGUAGE.containsKey(ext)) {
                    return EXTENSION_TO_LANGUAGE.get(ext);
                }
            }
        }

        return "UNKNOWN";
    }
}
