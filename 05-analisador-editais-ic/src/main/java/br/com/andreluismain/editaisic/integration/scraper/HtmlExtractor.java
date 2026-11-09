package br.com.andreluismain.editaisic.integration.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrator e normalizador de conteúdo HTML baseado em Jsoup.
 *
 * <p>Remove elementos não informativos (scripts, estilos, cabeçalhos de navegação e rodapés),
 * extrai título, instituição, datas relevantes e calcula o hash SHA-256 do texto limpo
 * para controle de idempotência e deduplicação.</p>
 */
@Component
public class HtmlExtractor {

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Processa a string HTML bruta e extrai o conteúdo textual relevante e metadados.
     *
     * @param html HTML cru da página coletada
     * @param sourceUrl URL de origem para fallback de título ou domínio
     * @return {@link ExtractedContent} normalizado
     */
    public ExtractedContent extract(String html, String sourceUrl) {
        if (html == null || html.isBlank()) {
            return new ExtractedContent("Sem título", null, "", generateSha256(""), null, null);
        }

        Document doc = Jsoup.parse(html, sourceUrl);

        // Extrai metadados antes de limpar a árvore DOM
        String title = extractTitle(doc);
        String institution = extractInstitution(doc);

        // Remove elementos visuais e irrelevantes para análise textual
        doc.select("script, style, nav, footer, header, aside, noscript, iframe, svg, button, form").remove();

        // Extrai texto limpo
        Element body = doc.body();
        String rawText = body != null ? body.text() : doc.text();

        // Normalização de espaços em branco
        String normalizedText = rawText.replaceAll("\\s+", " ").trim();

        // Tenta inferir datas de prazo no texto (ex: prazo, limite, encerramento)
        Instant deadline = extractDeadline(normalizedText);

        // Calcula hash SHA-256 do texto normalizado
        String contentHash = generateSha256(normalizedText);

        return new ExtractedContent(
                title != null && !title.isBlank() ? title : "Edital / Oportunidade Acadêmica",
                institution,
                normalizedText,
                contentHash,
                Instant.now(),
                deadline
        );
    }

    private String extractTitle(Document doc) {
        String title = doc.title();
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        Element h1 = doc.selectFirst("h1");
        if (h1 != null && !h1.text().isBlank()) {
            return h1.text().trim();
        }
        Element metaTitle = doc.selectFirst("meta[property=og:title]");
        if (metaTitle != null && metaTitle.hasAttr("content")) {
            return metaTitle.attr("content").trim();
        }
        return "Edital";
    }

    private String extractInstitution(Document doc) {
        Element metaSite = doc.selectFirst("meta[property=og:site_name]");
        if (metaSite != null && !metaSite.attr("content").isBlank()) {
            return metaSite.attr("content").trim();
        }
        Element metaAuthor = doc.selectFirst("meta[name=author]");
        if (metaAuthor != null && !metaAuthor.attr("content").isBlank()) {
            return metaAuthor.attr("content").trim();
        }
        return null;
    }

    private Instant extractDeadline(String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = DATE_PATTERN.matcher(text);
        Instant latestDate = null;
        while (matcher.find()) {
            try {
                String dateStr = matcher.group(1);
                LocalDate parsed = LocalDate.parse(dateStr, DATE_FORMATTER);
                Instant instant = parsed.atStartOfDay().toInstant(ZoneOffset.UTC);
                if (latestDate == null || instant.isAfter(latestDate)) {
                    latestDate = instant;
                }
            } catch (Exception ignored) {
                // Continua buscando outras datas
            }
        }
        return latestDate;
    }

    /**
     * Gera o hash SHA-256 de uma string para deduplicação de conteúdo.
     */
    public String generateSha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 não disponível no runtime", e);
        }
    }
}
