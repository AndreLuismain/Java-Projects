package br.com.andreluismain.editaisic.domain.model;

/**
 * Classificação de aderência entre um edital e o perfil do estudante/pesquisador.
 */
public enum MatchClassification {
    /**
     * Alta aderência aos requisitos e objetivos do perfil (score aproximado >= 75%).
     */
    HIGH_MATCH,

    /**
     * Média aderência aos requisitos (score aproximado entre 45% e 74%).
     */
    MEDIUM_MATCH,

    /**
     * Baixa aderência aos requisitos ou áreas não prioritárias (score < 45%).
     */
    LOW_MATCH,

    /**
     * Dados textuais insuficientes no edital para determinar a compatibilidade.
     */
    INSUFFICIENT_DATA
}
