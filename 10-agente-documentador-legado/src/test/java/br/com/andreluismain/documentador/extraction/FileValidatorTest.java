package br.com.andreluismain.documentador.extraction;

import br.com.andreluismain.documentador.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator();
        fileValidator.setMaxFileSize(1024 * 1024); // 1MB
    }

    @Test
    @DisplayName("Deve rejeitar arquivo nulo ou vazio")
    void shouldRejectEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "teste.cbl", "text/plain", new byte[0]);

        assertThrows(InvalidFileException.class, () -> fileValidator.validate(emptyFile));
    }

    @Test
    @DisplayName("Deve bloquear tentativa de path traversal no nome do arquivo")
    void shouldBlockPathTraversal() {
        MockMultipartFile traversalFile = new MockMultipartFile(
                "file", "../../../etc/passwd.cbl", "text/plain", "DISPLAY 'HELLO'.".getBytes(StandardCharsets.UTF_8));

        assertThrows(InvalidFileException.class, () -> fileValidator.validate(traversalFile));
    }

    @Test
    @DisplayName("Deve rejeitar extensão de arquivo não permitida")
    void shouldRejectDisallowedExtension() {
        MockMultipartFile exeFile = new MockMultipartFile(
                "file", "script.exe", "application/octet-stream", "dummy binary".getBytes());

        assertThrows(InvalidFileException.class, () -> fileValidator.validate(exeFile));
    }

    @Test
    @DisplayName("Deve aceitar arquivo com extensão válida e calcular hash SHA-256")
    void shouldAcceptValidFileAndComputeHash() {
        byte[] content = "IDENTIFICATION DIVISION.\nPROGRAM-ID. HELLO.".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile validFile = new MockMultipartFile("file", "hello.cbl", "text/plain", content);

        assertDoesNotThrow(() -> fileValidator.validate(validFile));

        String hash = fileValidator.calculateSha256(content);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    @DisplayName("Deve sanitizar credenciais aparentes no código-fonte")
    void shouldSanitizeCredentials() {
        String code = "String apiKey = 'secret123';\nString password = \"mypassword\";";
        String sanitized = fileValidator.sanitizeSourceCode(code);

        assertFalse(sanitized.contains("secret123"));
        assertFalse(sanitized.contains("mypassword"));
        assertTrue(sanitized.contains("[REDACTED_CREDENTIAL]"));
    }
}
