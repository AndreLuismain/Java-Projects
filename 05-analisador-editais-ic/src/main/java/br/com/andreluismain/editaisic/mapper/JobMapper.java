package br.com.andreluismain.editaisic.mapper;

import br.com.andreluismain.editaisic.domain.model.Job;
import br.com.andreluismain.editaisic.dto.response.JobResponse;
import org.springframework.stereotype.Component;

/**
 * Conversor entre entidade {@link Job} e seu DTO de acompanhamento.
 */
@Component
public class JobMapper {

    public JobResponse toResponse(Job entity) {
        return JobResponse.builder()
                .jobId(entity.getId())
                .profileId(entity.getProfile().getId())
                .status(entity.getStatus())
                .totalUrls(entity.getTotalUrls())
                .processedUrls(entity.getProcessedUrls())
                .failedUrls(entity.getFailedUrls())
                .errorDetails(entity.getErrorDetails())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
