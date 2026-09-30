package br.com.andreluismain.relatoriosic.domain.service;

import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import br.com.andreluismain.relatoriosic.domain.repository.CategorizedActivityRepository;
import br.com.andreluismain.relatoriosic.domain.repository.ResearchProjectRepository;
import br.com.andreluismain.relatoriosic.domain.repository.WeeklyLogRepository;
import br.com.andreluismain.relatoriosic.dto.request.CreateWeeklyLogRequest;
import br.com.andreluismain.relatoriosic.dto.response.CategorizedActivityResponse;
import br.com.andreluismain.relatoriosic.dto.response.WeeklyLogResponse;
import br.com.andreluismain.relatoriosic.exception.ConflictException;
import br.com.andreluismain.relatoriosic.exception.InvalidRequestException;
import br.com.andreluismain.relatoriosic.exception.ResourceNotFoundException;
import br.com.andreluismain.relatoriosic.integration.llm.LlmClassificationDto;
import br.com.andreluismain.relatoriosic.integration.llm.LlmReportGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo registro e classificação inteligente de logs semanais de pesquisa.
 */
@Service
public class WeeklyLogService {

    private static final Logger log = LoggerFactory.getLogger(WeeklyLogService.class);

    private final WeeklyLogRepository weeklyLogRepository;
    private final ResearchProjectRepository projectRepository;
    private final CategorizedActivityRepository activityRepository;
    private final LlmReportGateway llmGateway;

    public WeeklyLogService(WeeklyLogRepository weeklyLogRepository,
                            ResearchProjectRepository projectRepository,
                            CategorizedActivityRepository activityRepository,
                            LlmReportGateway llmGateway) {
        this.weeklyLogRepository = weeklyLogRepository;
        this.projectRepository = projectRepository;
        this.activityRepository = activityRepository;
        this.llmGateway = llmGateway;
    }

    @Transactional
    public WeeklyLogResponse submitWeeklyLog(CreateWeeklyLogRequest request) {
        if (!projectRepository.existsById(request.projectId())) {
            throw new ResourceNotFoundException("Projeto com id '" + request.projectId() + "' não encontrado.");
        }

        if (weeklyLogRepository.findByProjectIdAndWeekNumber(request.projectId(), request.weekNumber()).isPresent()) {
            throw new ConflictException("Já existe um log cadastrado para a semana " + request.weekNumber() + " deste projeto.");
        }

        if (!request.endDate().isAfter(request.startDate())) {
            throw new InvalidRequestException("Data final da semana deve ser posterior à data inicial.");
        }

        WeeklyLog weeklyLog = new WeeklyLog(
                UUID.randomUUID(),
                request.projectId(),
                request.weekNumber(),
                request.startDate(),
                request.endDate(),
                request.rawNotes()
        );
        WeeklyLog savedLog = weeklyLogRepository.save(weeklyLog);

        // Classificação das notas brutas através do agente LLM
        LlmClassificationDto classification = llmGateway.classifyActivities(savedLog);
        List<CategorizedActivity> activities = classification.activities().stream()
                .map(dto -> new CategorizedActivity(
                        UUID.randomUUID(),
                        savedLog.getId(),
                        dto.description(),
                        dto.category(),
                        dto.hoursSpent(),
                        dto.evidence()
                ))
                .toList();

        List<CategorizedActivity> savedActivities = activityRepository.saveAll(activities);
        log.info("Log semanal {} registrado e classificado com {} atividades.",
                savedLog.getWeekNumber(), savedActivities.size());

        List<CategorizedActivityResponse> actResponses = savedActivities.stream()
                .map(CategorizedActivityResponse::from)
                .toList();

        return WeeklyLogResponse.of(savedLog, actResponses);
    }

    @Transactional(readOnly = true)
    public WeeklyLogResponse getWeeklyLog(UUID id) {
        WeeklyLog log = weeklyLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log semanal com id '" + id + "' não encontrado."));

        List<CategorizedActivityResponse> activities = activityRepository.findByWeeklyLogId(id).stream()
                .map(CategorizedActivityResponse::from)
                .toList();

        return WeeklyLogResponse.of(log, activities);
    }

    @Transactional(readOnly = true)
    public List<WeeklyLogResponse> listLogsByProject(UUID projectId) {
        return weeklyLogRepository.findByProjectIdOrderByWeekNumberAsc(projectId).stream()
                .map(log -> {
                    List<CategorizedActivityResponse> acts = activityRepository.findByWeeklyLogId(log.getId()).stream()
                            .map(CategorizedActivityResponse::from)
                            .toList();
                    return WeeklyLogResponse.of(log, acts);
                })
                .toList();
    }
}
