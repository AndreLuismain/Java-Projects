package br.com.andreluismain.documentador.domain.model;

/**
 * Estados do ciclo de vida da análise de código legado.
 */
public enum AnalysisStatus {
    RECEIVED,
    PROCESSING,
    COMPLETED,
    FAILED
}
