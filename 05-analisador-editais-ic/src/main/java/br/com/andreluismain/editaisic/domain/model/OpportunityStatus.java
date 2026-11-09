package br.com.andreluismain.editaisic.domain.model;

/**
 * Estados do ciclo de vida de uma oportunidade coletada.
 */
public enum OpportunityStatus {
    /**
     * Oportunidade coletada e normalizada com sucesso via web scraping.
     */
    COLLECTED,

    /**
     * Oportunidade já submetida e analisada com sucesso pelo modelo de IA.
     */
    ANALYZED,

    /**
     * Falha na coleta, parsing ou integração externa.
     */
    FAILED
}
