package br.com.andreluismain.editaisic.integration;

import br.com.andreluismain.editaisic.integration.scraper.ExtractedContent;
import br.com.andreluismain.editaisic.integration.scraper.HtmlExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração - Extrator HTML")
class HtmlExtractorTest {

    private HtmlExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new HtmlExtractor();
    }

    @Test
    @DisplayName("Deve extrair título, sanitizar scripts e gerar hash do conteúdo textual")
    void shouldExtractAndSanitizeHtml() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Edital PIBIC 2026 - USP</title>
                    <meta property="og:site_name" content="Universidade de São Paulo" />
                    <script>alert('malicioso');</script>
                    <style>body { color: red; }</style>
                </head>
                <body>
                    <header><nav>Link 1 | Link 2</nav></header>
                    <main>
                        <h1>Bolsa de Iniciação Científica em Inteligência Artificial</h1>
                        <p>Inscrições abertas até 15/10/2026 para estudantes de graduação.</p>
                        <p>Requisitos: dedicação de 20 horas semanais e conhecimento em Java e IA.</p>
                    </main>
                    <footer>Todos os direitos reservados</footer>
                </body>
                </html>
                """;

        ExtractedContent extracted = extractor.extract(html, "https://usp.br/edital-pibic");

        assertNotNull(extracted);
        assertEquals("Edital PIBIC 2026 - USP", extracted.title());
        assertEquals("Universidade de São Paulo", extracted.institution());
        assertFalse(extracted.rawContent().contains("malicioso"));
        assertFalse(extracted.rawContent().contains("color: red"));
        assertFalse(extracted.rawContent().contains("Link 1"));
        assertTrue(extracted.rawContent().contains("Inscrições abertas até 15/10/2026"));
        assertNotNull(extracted.contentHash());
        assertEquals(64, extracted.contentHash().length());
        assertNotNull(extracted.deadlineAt());
    }

    @Test
    @DisplayName("Deve gerar o mesmo hash para o mesmo conteúdo normalizado")
    void shouldProduceReproducibleHash() {
        String text = "Edital de Pesquisa Acadêmica FAPESP 2026";
        String hash1 = extractor.generateSha256(text);
        String hash2 = extractor.generateSha256(text);

        assertEquals(hash1, hash2);
    }
}
