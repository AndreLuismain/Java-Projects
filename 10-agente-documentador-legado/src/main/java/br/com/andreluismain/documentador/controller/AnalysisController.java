package br.com.andreluismain.documentador.controller;

import br.com.andreluismain.documentador.domain.model.AnalysisStatus;
import br.com.andreluismain.documentador.domain.service.AnalysisService;
import br.com.andreluismain.documentador.dto.response.AnalysisInitiatedResponse;
import br.com.andreluismain.documentador.dto.response.AnalysisResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller REST para submissão, consulta e recuperação de documentação de código legado.
 */
@RestController
@RequestMapping("/api/v1/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Submete um arquivo de código legado para análise assíncrona/processamento.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisInitiatedResponse> submitAnalysis(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language) {
        AnalysisInitiatedResponse response = analysisService.submitAnalysis(file, language);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Consulta os metadados e status de uma análise pelo identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable UUID id) {
        return ResponseEntity.ok(analysisService.getAnalysisById(id));
    }

    /**
     * Recupera o documento técnico gerado em formato Markdown.
     */
    @GetMapping(value = "/{id}/document", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> getDocument(
            @PathVariable UUID id,
            @RequestParam(value = "format", defaultValue = "markdown") String format) {
        String markdown = analysisService.getGeneratedMarkdown(id);
        return ResponseEntity.ok(markdown);
    }

    /**
     * Lista o histórico de análises cadastradas de forma paginada e filtrada.
     */
    @GetMapping
    public ResponseEntity<Page<AnalysisResponse>> listAnalyses(
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "status", required = false) AnalysisStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(analysisService.listAnalyses(language, status, pageable));
    }

    /**
     * Exclui uma análise e seus documentos gerados.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable UUID id) {
        analysisService.deleteAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
