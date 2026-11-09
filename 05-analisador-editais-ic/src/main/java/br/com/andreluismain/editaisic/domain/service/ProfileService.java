package br.com.andreluismain.editaisic.domain.service;

import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.domain.repository.ProfileRepository;
import br.com.andreluismain.editaisic.dto.request.CreateProfileRequest;
import br.com.andreluismain.editaisic.dto.response.ProfileResponse;
import br.com.andreluismain.editaisic.exception.ResourceNotFoundException;
import br.com.andreluismain.editaisic.mapper.ProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de gerenciamento de perfis de estudantes e pesquisadores.
 */
@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Cadastra um novo perfil acadêmico.
     *
     * @param request dados do perfil
     * @return DTO com o perfil criado e timestamps
     */
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request) {
        log.info("Cadastrando novo perfil acadêmico: {}", request.getName());
        Profile profile = profileMapper.toEntity(request);
        Profile saved = profileRepository.save(profile);
        return profileMapper.toResponse(saved);
    }

    /**
     * Busca um perfil pelo seu identificador único.
     *
     * @param id UUID do perfil
     * @return DTO com os detalhes do perfil
     * @throws ResourceNotFoundException se não for encontrado
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(UUID id) {
        Profile profile = findEntityById(id);
        return profileMapper.toResponse(profile);
    }

    /**
     * Busca a entidade Profile gerenciada ou lança exceção.
     */
    @Transactional(readOnly = true)
    public Profile findEntityById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado com o ID: " + id));
    }
}
