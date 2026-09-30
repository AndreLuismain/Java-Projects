package br.com.andreluismain.documentador.integration.llm;

/**
 * Gateway de integração desacoplado para modelos de linguagem (LLM).
 */
public interface LlmDocumenterGateway {

    /**
     * Aciona o agente LLM para analisar o código e produzir documentação técnica em Markdown.
     */
    String generateDocumentation(String fileName, String language, String sanitizedSourceCode);

    /**
     * Retorna o identificador do modelo em uso.
     */
    String getModelName();
}
