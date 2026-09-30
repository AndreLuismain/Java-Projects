package br.com.andreluismain.recomendacao.controller;

import br.com.andreluismain.recomendacao.domain.service.RecommendationService;
import br.com.andreluismain.recomendacao.dto.request.RunRecommendationRequest;
import br.com.andreluismain.recomendacao.dto.response.ImportResultResponse;
import br.com.andreluismain.recomendacao.dto.response.RecommendationResponse;
import br.com.andreluismain.recomendacao.dto.response.RecommendationRunInitiatedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para execução de recomendações e importação ETL de oportunidades de pesquisa.
 */
@RestController
@RequestMapping("/api/v1")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Dispara a execução do pipeline de recomendação para um perfil e oportunidades.
     */
    @PostMapping("/recommendations")
    public ResponseEntity<RecommendationRunInitiatedResponse> runRecommendations(
            @Valid @RequestBody RunRecommendationRequest request) {
        RecommendationRunInitiatedResponse response = recommendationService.runRecommendation(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Consulta o ranking de recomendações gerado para a execução.
     */
    @GetMapping("/recommendations/{runId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable UUID runId,
            @RequestParam(value = "minScore", required = false) Double minScore,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(recommendationService.getRecommendations(runId, minScore, pageable));
    }

    /**
     * Importa em lote e deduplica oportunidades de pesquisa acadêmica via pipeline ETL.
     */
    @PostMapping("/opportunities/import")
    public ResponseEntity<ImportResultResponse> importOpportunities(
            @RequestBody List<Map<String, Object>> opportunities) {
        ImportResultResponse response = recommendationService.importOpportunities(opportunities);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
