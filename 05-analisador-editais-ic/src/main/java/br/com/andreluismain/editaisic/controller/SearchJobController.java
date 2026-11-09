package br.com.andreluismain.editaisic.controller;

import br.com.andreluismain.editaisic.domain.service.JobService;
import br.com.andreluismain.editaisic.dto.request.SearchBatchRequest;
import br.com.andreluismain.editaisic.dto.response.ErrorResponse;
import br.com.andreluismain.editaisic.dto.response.JobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para processamento assíncrono em lote (Batch Search) e acompanhamento de jobs.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Busca em Lote e Jobs", description = "Endpoints para disparo e consulta de tarefas em lote de coleta e análise")
public class SearchJobController {

    private final JobService jobService;

    public SearchJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/search")
    @Operation(summary = "Submeter lote de URLs para coleta e análise", description = "Enfileira um conjunto de URLs para processamento assíncrono em background.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Job enfileirado com sucesso",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<JobResponse> submitBatch(@Valid @RequestBody SearchBatchRequest request) {
        JobResponse response = jobService.createAndStartJob(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/jobs/{id}")
    @Operation(summary = "Consultar status do job", description = "Retorna métricas de progresso, quantidade de URLs processadas e eventuais erros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do job retornado",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "404", description = "Job não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<JobResponse> getJobStatus(@PathVariable UUID id) {
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(response);
    }
}
