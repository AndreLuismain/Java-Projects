package br.com.andreluismain.editaisic.integration;

import br.com.andreluismain.editaisic.exception.SsrfSecurityException;
import br.com.andreluismain.editaisic.integration.scraper.SsrfValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Segurança - Validação contra SSRF")
class SsrfValidatorTest {

    private SsrfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SsrfValidator();
    }

    @ParameterizedTest(name = "Deve bloquear URL perigosa: {0}")
    @ValueSource(strings = {
            "http://localhost:8080/admin",
            "http://127.0.0.1:5432",
            "http://127.0.0.1",
            "ftp://exemplo.org/arquivo.pdf",
            "file:///etc/passwd",
            "javascript:alert(1)"
    })
    void shouldBlockDangerousUrls(String url) {
        assertThrows(SsrfSecurityException.class, () -> validator.validateUrl(url));
    }

    @Test
    @DisplayName("Deve permitir URL pública com esquema HTTPS")
    void shouldAllowValidPublicUrl() {
        assertDoesNotThrow(() -> validator.validateUrl("https://example.org/edital-ic-2026"));
    }

    @Test
    @DisplayName("Deve lançar exceção para URL nula ou vazia")
    void shouldThrowForEmptyUrl() {
        assertThrows(SsrfSecurityException.class, () -> validator.validateUrl(""));
        assertThrows(SsrfSecurityException.class, () -> validator.validateUrl(null));
    }
}
