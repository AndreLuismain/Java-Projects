package br.com.andreluismain.relatoriosic.domain.service;

import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;
import br.com.andreluismain.relatoriosic.domain.repository.ResearchProjectRepository;
import br.com.andreluismain.relatoriosic.dto.request.CreateProjectRequest;
import br.com.andreluismain.relatoriosic.dto.response.ProjectResponse;
import br.com.andreluismain.relatoriosic.exception.InvalidRequestException;
import br.com.andreluismain.relatoriosic.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de gerenciamento de projetos de Iniciação Científica.
 */
@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ResearchProjectRepository projectRepository;

    public ProjectService(ResearchProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new InvalidRequestException("Data de término deve ser posterior à data de início.");
        }

        ResearchProject project = new ResearchProject(
                UUID.randomUUID(),
                request.title(),
                request.studentName(),
                request.advisorName(),
                request.grantAgency(),
                request.startDate(),
                request.endDate()
        );

        ResearchProject saved = projectRepository.save(project);
        log.info("Projeto de IC criado: id={}, titulo={}", saved.getId(), saved.getTitle());
        return ProjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(ProjectResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto com id '" + id + "' não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::from)
                .toList();
    }
}
