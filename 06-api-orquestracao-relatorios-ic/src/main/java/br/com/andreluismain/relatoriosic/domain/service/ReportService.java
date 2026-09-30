package br.com.andreluismain.relatoriosic.domain.service;

import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import br.com.andreluismain.relatoriosic.domain.repository.CategorizedActivityRepository;
import br.com.andreluismain.relatoriosic.domain.repository.ResearchProjectRepository;
import br.com.andreluismain.relatoriosic.domain.repository.WeeklyLogRepository;
import br.com.andreluismain.relatoriosic.exception.ResourceNotFoundException;
import br.com.andreluismain.relatoriosic.report.MarkdownReportGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Serviço responsável pela consolidação e orquestração do relatório final de Iniciação Científica em Markdown.
 */
@Service
public class ReportService {

    private final ResearchProjectRepository projectRepository;
    private final WeeklyLogRepository weeklyLogRepository;
    private final CategorizedActivityRepository activityRepository;
    private final MarkdownReportGenerator markdownReportGenerator;

    public ReportService(ResearchProjectRepository projectRepository,
                         WeeklyLogRepository weeklyLogRepository,
                         CategorizedActivityRepository activityRepository,
                         MarkdownReportGenerator markdownReportGenerator) {
        this.projectRepository = projectRepository;
        this.weeklyLogRepository = weeklyLogRepository;
        this.activityRepository = activityRepository;
        this.markdownReportGenerator = markdownReportGenerator;
    }

    @Transactional(readOnly = true)
    public String generateReport(UUID projectId) {
        ResearchProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto com id '" + projectId + "' não encontrado."));

        List<WeeklyLog> logs = weeklyLogRepository.findByProjectIdOrderByWeekNumberAsc(projectId);

        Map<UUID, List<CategorizedActivity>> activitiesByLog = new HashMap<>();
        for (WeeklyLog log : logs) {
            List<CategorizedActivity> acts = activityRepository.findByWeeklyLogId(log.getId());
            activitiesByLog.put(log.getId(), acts);
        }

        return markdownReportGenerator.generateReport(project, logs, activitiesByLog);
    }
}
