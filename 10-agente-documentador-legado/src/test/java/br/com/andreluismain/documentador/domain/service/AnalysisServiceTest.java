package br.com.andreluismain.documentador.domain.service;

import br.com.andreluismain.documentador.domain.model.Analysis;
import br.com.andreluismain.documentador.domain.model.AnalysisError;
import br.com.andreluismain.documentador.domain.model.AnalysisStatus;
import br.com.andreluismain.documentador.domain.model.GeneratedDocument;
import br.com.andreluismain.documentador.dto.response.AnalysisInitiatedResponse;
import br.com.andreluismain.documentador.exception.DocumentNotReadyException;
import br.com.andreluismain.documentador.extraction.FileValidator;
import br.com.andreluismain.documentador.extraction.LanguageDetector;
import br.com.andreluismain.documentador.integration.llm.LlmDocumenterGateway;
import br.com.andreluismain.documentador.repository.AnalysisErrorRepository;
import br.com.andreluismain.documentador.repository.AnalysisRepository;
import br.com.andreluismain.documentador.repository.GeneratedDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private GeneratedDocumentRepository documentRepository;

    @Mock
    private AnalysisErrorRepository errorRepository;

    @Mock
    private LlmDocumenterGateway llmGateway;

    private final FileValidator fileValidator = new FileValidator();
    private final LanguageDetector languageDetector = new LanguageDetector();

    private AnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new AnalysisService(
                analysisRepository,
                documentRepository,
                errorRepository,
                fileValidator,
                languageDetector,
                llmGateway
        );
        analysisService.setPromptVersion("v1.0");
    }

    @Test
    @DisplayName("Deve submeter arquivo, chamar LLM e gerar documento com status COMPLETED")
    void shouldSubmitAndGenerateDocumentationSuccessfully() {
        String sourceCode = "int main() { printf(\"Legacy code\\n\"); return 0; }";
        MockMultipartFile file = new MockMultipartFile("file", "main.c", "text/plain",
                sourceCode.getBytes(StandardCharsets.UTF_8));

        when(documentRepository.findFirstByContentHashAndPromptVersion(anyString(), eq("v1.0")))
                .thenReturn(Optional.empty());
        when(llmGateway.generateDocumentation(eq("main.c"), eq("C"), anyString()))
                .thenReturn("# Documentacao Tecnica C");
        when(llmGateway.getModelName()).thenReturn("gemini-1.5-pro");

        AnalysisInitiatedResponse response = analysisService.submitAnalysis(file, null);

        assertNotNull(response);
        assertEquals(AnalysisStatus.PROCESSING, response.status());

        ArgumentCaptor<Analysis> analysisCaptor = ArgumentCaptor.forClass(Analysis.class);
        verify(analysisRepository, atLeastOnce()).save(analysisCaptor.capture());
        Analysis savedAnalysis = analysisCaptor.getValue();
        assertEquals(AnalysisStatus.COMPLETED, savedAnalysis.getStatus());
        assertEquals("C", savedAnalysis.getLanguage());

        verify(documentRepository).save(any(GeneratedDocument.class));
    }

    @Test
    @DisplayName("Deve reaproveitar documentação em cache para mesmo contentHash e versão")
    void shouldReuseCachedDocumentationForSameHash() {
        String sourceCode = "def process(): pass";
        MockMultipartFile file = new MockMultipartFile("file", "script.py", "text/plain",
                sourceCode.getBytes(StandardCharsets.UTF_8));

        GeneratedDocument cachedDoc = new GeneratedDocument(
                UUID.randomUUID(), UUID.randomUUID(), "# Doc Python em Cache", "gemini-1.5-pro", "v1.0", "hash123");

        when(documentRepository.findFirstByContentHashAndPromptVersion(anyString(), eq("v1.0")))
                .thenReturn(Optional.of(cachedDoc));

        analysisService.submitAnalysis(file, "PYTHON");

        // LLM não deve ter sido invocado pois havia cache
        verify(llmGateway, never()).generateDocumentation(any(), any(), any());
        verify(documentRepository).save(any(GeneratedDocument.class));
    }

    @Test
    @DisplayName("Deve marcar como FAILED e registrar erro se LLM falhar")
    void shouldHandleLlmFailureGracefully() {
        String sourceCode = "SELECT * FROM legacy_table;";
        MockMultipartFile file = new MockMultipartFile("file", "query.sql", "text/plain",
                sourceCode.getBytes(StandardCharsets.UTF_8));

        when(documentRepository.findFirstByContentHashAndPromptVersion(anyString(), eq("v1.0")))
                .thenReturn(Optional.empty());
        when(llmGateway.generateDocumentation(any(), any(), any()))
                .thenThrow(new RuntimeException("LLM Gateway timeout"));

        analysisService.submitAnalysis(file, "SQL");

        ArgumentCaptor<Analysis> captor = ArgumentCaptor.forClass(Analysis.class);
        verify(analysisRepository, atLeastOnce()).save(captor.capture());
        assertEquals(AnalysisStatus.FAILED, captor.getValue().getStatus());

        verify(errorRepository).save(any(AnalysisError.class));
    }

    @Test
    @DisplayName("Deve lançar DocumentNotReadyException ao solicitar documento de análise não concluída")
    void shouldThrowWhenDocumentNotReady() {
        UUID id = UUID.randomUUID();
        Analysis processingAnalysis = new Analysis(id, "main.c", "C", 100L, "hash", "code");
        processingAnalysis.markProcessing();

        when(analysisRepository.findById(id)).thenReturn(Optional.of(processingAnalysis));

        assertThrows(DocumentNotReadyException.class, () -> analysisService.getGeneratedMarkdown(id));
    }
}
