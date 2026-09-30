package br.com.andreluismain.agendamento.domain.service;

import br.com.andreluismain.agendamento.domain.model.Resource;
import br.com.andreluismain.agendamento.dto.request.CreateResourceRequest;
import br.com.andreluismain.agendamento.dto.response.ResourceResponse;
import br.com.andreluismain.agendamento.exception.ResourceNotFoundException;
import br.com.andreluismain.agendamento.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo cadastro e consulta de recursos acadêmicos.
 */
@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public ResourceResponse createResource(CreateResourceRequest request) {
        Resource resource = Resource.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .type(request.type())
                .capacity(request.capacity())
                .active(true)
                .build();

        Resource saved = resourceRepository.save(resource);
        log.info("Recurso cadastrado com sucesso: id={}, nome={}", saved.getId(), saved.getName());
        return ResourceResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(UUID id) {
        return resourceRepository.findById(id)
                .map(ResourceResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> listResources() {
        return resourceRepository.findAll().stream()
                .map(ResourceResponse::from)
                .toList();
    }
}
