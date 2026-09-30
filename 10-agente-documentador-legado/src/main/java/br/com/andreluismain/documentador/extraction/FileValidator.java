package br.com.andreluismain.documentador.extraction;

import br.com.andreluismain.documentador.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador e sanitizador de integridade e segurança de arquivos enviados para documentação.
 * Bloqueia path traversal, extensões não autorizadas, credenciais e arquivos binários.
 */
@Component
public class FileValidator {

    private static final List<String> DEFAULT_ALLOWED_EXTENSIONS = List.of(
            "java", "c", "cpp", "cbl", "cob", "pas", "py", "js", "sql", "sh", "php"
    );

    private static final Pattern CREDENTIAL_PATTERN = Pattern.compile(
            "(?i)(api[_-]?key|secret|password|passwd|token)\\s*[:=]\\s*['\"]?([^'\"\\s]+)['\"]?"
    );

    @Value("${documentador.max-file-size:2097152}")
    private long maxFileSize = 2097152L; // 2MB

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Arquivo de código-fonte é obrigatório e não pode estar vazio.");
        }

        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException("Tamanho do arquivo excede o limite máximo permitido de " + maxFileSize + " bytes.");
        }

        String rawFilename = file.getOriginalFilename();
        if (rawFilename == null || rawFilename.isBlank()) {
            throw new InvalidFileException("Nome do arquivo não informado.");
        }

        // Bloqueio rigoroso de path traversal e caracteres inseguros
        if (rawFilename.contains("..") || rawFilename.contains("/") || rawFilename.contains("\\") || rawFilename.startsWith(".")) {
            throw new InvalidFileException("Nome do arquivo inválido ou tentativa de path traversal detectada.");
        }

        // Validação de extensão
        String extension = extractExtension(rawFilename);
        if (!DEFAULT_ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException("Extensão de arquivo não permitida (." + extension + "). Extensões suportadas: " +
                    String.join(", ", DEFAULT_ALLOWED_EXTENSIONS));
        }
    }

    /**
     * Calcula o hash SHA-256 do conteúdo do arquivo para garantia de integridade e idempotência.
     */
    public String calculateSha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 não disponível no runtime", e);
        }
    }

    /**
     * Sanitiza o código-fonte removendo senhas e chaves de API aparentes antes do envio ao LLM.
     */
    public String sanitizeSourceCode(String content) {
        if (content == null) {
            return "";
        }
        return CREDENTIAL_PATTERN.matcher(content).replaceAll("$1 = [REDACTED_CREDENTIAL]");
    }

    public String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
