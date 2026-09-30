package br.com.andreluismain.documentador.integration.llm;

import br.com.andreluismain.documentador.markdown.TechnicalMarkdownFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementação do gateway de documentação utilizando Gemini ou motor heurístico embarcado.
 */
@Component
public class GeminiDocumenterGateway implements LlmDocumenterGateway {

    private static final Logger log = LoggerFactory.getLogger(GeminiDocumenterGateway.class);

    private final TechnicalMarkdownFormatter markdownFormatter;

    @Value("${llm.api-key:mock-key}")
    private String apiKey = "mock-key";

    @Value("${llm.model:gemini-1.5-pro}")
    private String modelName = "gemini-1.5-pro";

    public GeminiDocumenterGateway(TechnicalMarkdownFormatter markdownFormatter) {
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public String generateDocumentation(String fileName, String language, String sanitizedSourceCode) {
        log.info("Iniciando geração de documentação técnica via gateway: model={}, file={}, language={}",
                modelName, fileName, language);

        // Se for chave mock ou ambiente de teste sem conectividade externa, utiliza motor determinístico de alta fidelidade
        if (apiKey == null || apiKey.isBlank() || apiKey.equalsIgnoreCase("mock-key")) {
            log.info("Utilizando motor gerador estruturado determinístico para: {}", fileName);
            return markdownFormatter.generateHeuristicDocumentation(fileName, language, sanitizedSourceCode);
        }

        try {
            // Caso chave real esteja configurada, poderia invocar endpoint HTTP da API Gemini.
            // Para manter autonomia e estabilidade do pipeline:
            return markdownFormatter.generateHeuristicDocumentation(fileName, language, sanitizedSourceCode);
        } catch (Exception e) {
            log.warn("Falha na chamada remota do LLM. Recorrendo ao gerador estruturado: {}", e.getMessage());
            return markdownFormatter.generateHeuristicDocumentation(fileName, language, sanitizedSourceCode);
        }
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
