package br.com.andreluismain.editaisic.domain.model;

/**
 * Estados de processamento de um job em lote (busca/análise assíncrona).
 */
public enum JobStatus {
    /**
     * Job recebido e enfileirado para execução em segundo plano.
     */
    QUEUED,

    /**
     * Job em processamento ativo pelas threads assíncronas.
     */
    PROCESSING,

    /**
     * Job finalizado com sucesso (todas ou grande parte das URLs processadas).
     */
    COMPLETED,

    /**
     * Falha crítica durante a execução do lote.
     */
    FAILED
}
