package br.com.andreluismain.editaisic.mapper;

import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.dto.request.CreateProfileRequest;
import br.com.andreluismain.editaisic.dto.response.ProfileResponse;
import org.springframework.stereotype.Component;

/**
 * Conversor entre entidades {@link Profile} e seus respectivos DTOs.
 */
@Component
public class ProfileMapper {

    public Profile toEntity(CreateProfileRequest request) {
        Profile profile = Profile.builder()
                .name(request.getName().trim())
                .description(request.getDescription().trim())
                .build();
        profile.setKeywordsList(request.getKeywords());
        return profile;
    }

    public ProfileResponse toResponse(Profile entity) {
        return ProfileResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .keywords(entity.getKeywordsList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
