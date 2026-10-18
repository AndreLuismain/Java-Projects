package br.com.andreluismain.documentador.domain.service;

import br.com.andreluismain.documentador.domain.model.Analysis;
import br.com.andreluismain.documentador.domain.model.AnalysisError;
import br.com.andreluismain.documentador.domain.model.AnalysisStatus;
import br.com.andreluismain.documentador.domain.model.GeneratedDocument;
import br.com.andreluismain.documentador.dto.response.AnalysisInitiatedResponse;
import br.com.andreluismain.documentador.dto.response.AnalysisResponse;
import br.com.andreluismain.documentador.exception.AnalysisNotFoundException;
import br.com.andreluismain.documentador.exception.DocumentNotReadyException;
import br.com.andreluismain.documentador.exception.InvalidFileException;
import br.com.andreluismain.documentador.extraction.FileValidator;
import br.com.andreluismain.documentador.extraction.LanguageDetector;
import br.com.andreluismain.documentador.integration.llm.LlmDocumenterGateway;
import br.com.andreluismain.documentador.repository.AnalysisErrorRepository;
import br.com.andreluismain.documentador.repository.AnalysisRepository;
import br.com.andreluismain.documentador.repository.GeneratedDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de orquestração do pipeline de análise e documentação de código legado.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final GeneratedDocumentRepository documentRepository;
    private final AnalysisErrorRepository errorRepository;
    private final FileValidator fileValidator;
    private final LanguageDetector languageDetector;
    private final LlmDocumenterGateway llmGateway;

    @Value("${documentador.prompt-version:v1.0}")
    private String promptVersion = "v1.0";

    public AnalysisService(AnalysisRepository analysisRepository,
                           GeneratedDocumentRepository documentRepository,
                           AnalysisErrorRepository errorRepository,
                           FileValidator fileValidator,
                           LanguageDetector languageDetector,
                           LlmDocumenterGateway llmGateway) {
        this.analysisRepository = analysisRepository;
        this.documentRepository = documentRepository;
        this.errorRepository = errorRepository;
        this.fileValidator = fileValidator;
        this.languageDetector = languageDetector;
        this.llmGateway = llmGateway;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    /**
     * Submete um arquivo de código para análise e geração técnica de documentação.
     */
    @Transactional
    public AnalysisInitiatedResponse submitAnalysis(MultipartFile file, String requestedLanguage) {
        fileValidator.validate(file);

        byte[] bytes;
        String rawContent;
        try {
            bytes = file.getBytes();
            rawContent = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InvalidFileException("Falha ao ler os bytes do arquivo enviado: " + e.getMessage());
        }

        String fileName = file.getOriginalFilename();
        String contentHash = fileValidator.calculateSha256(bytes);
        String language = languageDetector.resolveLanguage(requestedLanguage, fileName);

        UUID analysisId = UUID.randomUUID();
        Analysis analysis = new Analysis(
                analysisId,
                fileName,
                language,
                file.getSize(),
                contentHash,
                rawContent
        );
        analysis.markProcessing();
        analysisRepository.save(analysis);

        // Processamento da documentação (com suporte a cache/reaproveitamento por hash)
        processDocumentation(analysis, rawContent, contentHash);

        return new AnalysisInitiatedResponse(analysis.getId(), AnalysisStatus.PROCESSING);
    }

    private void processDocumentation(Analysis analysis, String rawContent, String contentHash) {
        try {
            // Verifica se já existe documentação gerada para o mesmo conteúdo e versão do modelo
            Optional<GeneratedDocument> cachedDoc = documentRepository
                    .findFirstByContentHashAndPromptVersion(contentHash, promptVersion);

            String markdown;
            String modelName;
            if (cachedDoc.isPresent()) {
                log.info("Reaproveitando documentação gerada para o hash {}", contentHash);
                markdown = cachedDoc.get().getMarkdown();
                modelName = cachedDoc.get().getModelName();
            } else {
                String sanitizedCode = fileValidator.sanitizeSourceCode(rawContent);
                markdown = llmGateway.generateDocumentation(analysis.getFileName(), analysis.getLanguage(), sanitizedCode);
                modelName = llmGateway.getModelName();
            }

            GeneratedDocument document = new GeneratedDocument(
                    UUID.randomUUID(),
                    analysis.getId(),
                    markdown,
                    modelName,
                    promptVersion,
                    contentHash
            );
            documentRepository.save(document);
            analysis.markCompleted();
            analysisRepository.save(analysis);

            log.info("Análise concluída com sucesso: analysisId={}", analysis.getId());
        } catch (Exception e) {
            log.error("Erro ao gerar documentação para a análise {}", analysis.getId(), e);
            analysis.markFailed();
            analysisRepository.save(analysis);

            AnalysisError error = new AnalysisError(
                    UUID.randomUUID(),
                    analysis.getId(),
                    "DOCUMENT_GENERATION_FAILED",
                    "Falha ao gerar documentação: " + e.getMessage()
            );
            errorRepository.save(error);
        }
    }

    /**
     * Consulta os metadados de uma análise pelo ID.
     */
    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysisById(UUID id) {
        return analysisRepository.findById(id)
                .map(AnalysisResponse::from)
                .orElseThrow(() -> new AnalysisNotFoundException(id));
    }

    /**
     * Recupera o documento técnico gerado em formato Markdown.
     */
    @Transactional(readOnly = true)
    public String getGeneratedMarkdown(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(id));

        if (analysis.getStatus() != AnalysisStatus.COMPLETED) {
            throw new DocumentNotReadyException("A documentação ainda não está pronta. Status atual: " + analysis.getStatus());
        }

        return documentRepository.findByAnalysisId(id)
                .map(GeneratedDocument::getMarkdown)
                .orElseThrow(() -> new DocumentNotReadyException("Documento não encontrado para a análise"));
    }

    /**
     * Lista o histórico paginado de análises com filtros.
     */
    @Transactional(readOnly = true)
    public Page<AnalysisResponse> listAnalyses(String language, AnalysisStatus status, Pageable pageable) {
        return analysisRepository.findByFilter(language, status, pageable)
                .map(AnalysisResponse::from);
    }

    /**
     * Exclui uma análise e seus artefatos mantendo integridade.
     */
    @Transactional
    public void deleteAnalysis(UUID id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(id));

        documentRepository.findByAnalysisId(id).ifPresent(documentRepository::delete);
        errorRepository.findByAnalysisId(id).ifPresent(errorRepository::delete);
        analysisRepository.delete(analysis);

        log.info("Análise excluída: id={}", id);
    }
}
