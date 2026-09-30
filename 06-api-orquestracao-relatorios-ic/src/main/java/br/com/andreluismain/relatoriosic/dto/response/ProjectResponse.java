package br.com.andreluismain.relatoriosic.dto.response;

import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta contendo informações do projeto acadêmico de IC.
 */
public record ProjectResponse(
        UUID id,
        String title,
        String studentName,
        String advisorName,
        String grantAgency,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {
    public static ProjectResponse from(ResearchProject project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getStudentName(),
                project.getAdvisorName(),
                project.getGrantAgency(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt()
        );
    }
}
