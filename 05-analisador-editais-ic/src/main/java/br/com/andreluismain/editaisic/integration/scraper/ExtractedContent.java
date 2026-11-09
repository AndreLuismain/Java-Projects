package br.com.andreluismain.editaisic.integration.scraper;

import java.time.Instant;

/**
 * Registro com os dados estruturados extraídos de uma página HTML de edital.
 */
public record ExtractedContent(
        String title,
        String institution,
        String rawContent,
        String contentHash,
        Instant publishedAt,
        Instant deadlineAt
) {}
