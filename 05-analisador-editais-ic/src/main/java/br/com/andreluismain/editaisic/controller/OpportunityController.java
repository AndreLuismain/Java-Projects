package br.com.andreluismain.editaisic.controller;

import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import br.com.andreluismain.editaisic.domain.service.AnalysisService;
import br.com.andreluismain.editaisic.domain.service.OpportunityService;
import br.com.andreluismain.editaisic.dto.request.AnalyzeOpportunityRequest;
import br.com.andreluismain.editaisic.dto.request.CollectOpportunityRequest;
import br.com.andreluismain.editaisic.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Controller REST para coleta, consulta e análise semântica de oportunidades acadêmicas.
 */
@RestController
@RequestMapping("/api/v1/opportunities")
@Tag(name = "Oportunidades e Editais", description = "Endpoints para coleta de editais na web e análise de aderência com Gemini")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final AnalysisService analysisService;

    public OpportunityController(
            OpportunityService opportunityService,
            AnalysisService analysisService
    ) {
        this.opportunityService = opportunityService;
        this.analysisService = analysisService;
    }

    @PostMapping("/collect")
    @Operation(summary = "Coletar novo edital da web", description = "Executa web scraping na URL informada, extrai e normaliza o texto da oportunidade.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Oportunidade coletada e normalizada com sucesso",
                    content = @Content(schema = @Schema(implementation = CollectOpportunityResponse.class))),
            @ApiResponse(responseCode = "400", description = "URL inválida ou tentativa de SSRF",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "URL ou conteúdo idêntico já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Falha ao conectar ou processar o HTML da página",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CollectOpportunityResponse> collectOpportunity(@Valid @RequestBody CollectOpportunityRequest request) {
        CollectOpportunityResponse response = opportunityService.collectOpportunity(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar oportunidades paginadas", description = "Retorna uma página de editais cadastrados com filtros opcionais de status e prazo limite.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de oportunidades retornada com sucesso")
    })
    public ResponseEntity<Page<OpportunityResponse>> listOpportunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deadlineBefore
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OpportunityResponse> result = opportunityService.listOpportunities(pageable, status, deadlineBefore);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar oportunidade por ID", description = "Recupera a oportunidade normalizada com o conteúdo textual bruto e hash SHA-256.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oportunidade encontrada",
                    content = @Content(schema = @Schema(implementation = OpportunityDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oportunidade não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OpportunityDetailResponse> getOpportunity(@PathVariable UUID id) {
        OpportunityDetailResponse response = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/analyze")
    @Operation(summary = "Solicitar análise semântica com Gemini", description = "Dispara a análise assíncrona de compatibilidade entre o edital e o perfil informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Análise aceita e encaminhada para processamento em background",
                    content = @Content(schema = @Schema(implementation = AnalyzeAcceptedResponse.class))),
            @ApiResponse(responseCode = "404", description = "Oportunidade ou perfil não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AnalyzeAcceptedResponse> analyzeOpportunity(
            @PathVariable UUID id,
            @Valid @RequestBody AnalyzeOpportunityRequest request
    ) {
        AnalyzeAcceptedResponse response = analysisService.requestAnalysis(id, request.getProfileId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}/analysis")
    @Operation(summary = "Consultar resultado da análise semântica", description = "Retorna o resumo, nota de aderência, classificação, requisitos e riscos gerados pela IA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado da análise retornado",
                    content = @Content(schema = @Schema(implementation = AnalysisResponse.class))),
            @ApiResponse(responseCode = "404", description = "Análise não encontrada para esta oportunidade/perfil",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AnalysisResponse> getAnalysis(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID profileId
    ) {
        AnalysisResponse response = analysisService.getAnalysis(id, profileId);
        return ResponseEntity.ok(response);
    }
}
