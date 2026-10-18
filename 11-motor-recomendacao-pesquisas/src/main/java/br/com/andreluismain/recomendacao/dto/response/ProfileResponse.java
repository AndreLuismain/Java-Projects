package br.com.andreluismain.recomendacao.dto.response;

import br.com.andreluismain.recomendacao.domain.model.StudentProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resposta com os dados cadastrais do perfil acadêmico do estudante.
 */
public record ProfileResponse(
        UUID id,
        Integer semester,
        List<String> technologies,
        List<String> researchAreas,
        String interests,
        Integer availabilityHours,
        LocalDateTime createdAt
) {
    public static ProfileResponse from(StudentProfile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getSemester(),
                profile.getTechnologies(),
                profile.getResearchAreas(),
                profile.getInterests(),
                profile.getAvailabilityHours(),
                profile.getCreatedAt()
        );
    }
}
