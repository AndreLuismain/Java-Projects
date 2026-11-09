package br.com.andreluismain.editaisic.dto.response;

import br.com.andreluismain.editaisic.domain.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO para acompanhamento do status e métricas de um job em lote.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private UUID jobId;
    private UUID profileId;
    private JobStatus status;
    private int totalUrls;
    private int processedUrls;
    private int failedUrls;
    private String errorDetails;
    private Instant createdAt;
    private Instant updatedAt;
}
