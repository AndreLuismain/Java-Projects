package br.com.andreluismain.relatoriosic;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;
import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import br.com.andreluismain.relatoriosic.domain.repository.CategorizedActivityRepository;
import br.com.andreluismain.relatoriosic.domain.repository.ResearchProjectRepository;
import br.com.andreluismain.relatoriosic.domain.repository.WeeklyLogRepository;
import br.com.andreluismain.relatoriosic.domain.service.WeeklyLogService;
import br.com.andreluismain.relatoriosic.dto.request.CreateWeeklyLogRequest;
import br.com.andreluismain.relatoriosic.dto.response.WeeklyLogResponse;
import br.com.andreluismain.relatoriosic.exception.ConflictException;
import br.com.andreluismain.relatoriosic.integration.llm.LlmActivityDto;
import br.com.andreluismain.relatoriosic.integration.llm.LlmClassificationDto;
import br.com.andreluismain.relatoriosic.integration.llm.LlmReportGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyLogServiceTest {

    @Mock
    private WeeklyLogRepository weeklyLogRepository;

    @Mock
    private ResearchProjectRepository projectRepository;

    @Mock
    private CategorizedActivityRepository activityRepository;

    @Mock
    private LlmReportGateway llmGateway;

    private WeeklyLogService weeklyLogService;

    private UUID projectId;

    @BeforeEach
    void setUp() {
        weeklyLogService = new WeeklyLogService(
                weeklyLogRepository,
                projectRepository,
                activityRepository,
                llmGateway
        );
        projectId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve registrar log semanal e acionar classificação automática via LLM Gateway")
    void shouldSubmitAndClassifyWeeklyLog() {
        CreateWeeklyLogRequest request = new CreateWeeklyLogRequest(
                projectId, 1, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 7),
                "Estudo de papers e desenvolvimento de scripts"
        );

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(weeklyLogRepository.findByProjectIdAndWeekNumber(projectId, 1)).thenReturn(Optional.empty());
        when(weeklyLogRepository.save(any(WeeklyLog.class))).thenAnswer(i -> i.getArgument(0));

        LlmClassificationDto classification = new LlmClassificationDto(List.of(
                new LlmActivityDto("Estudo de papers", ActivityCategory.STUDY, 4, "notas"),
                new LlmActivityDto("Desenvolvimento de scripts", ActivityCategory.DEVELOPMENT, 6, "repo")
        ));
        when(llmGateway.classifyActivities(any(WeeklyLog.class))).thenReturn(classification);
        when(activityRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        WeeklyLogResponse response = weeklyLogService.submitWeeklyLog(request);

        assertNotNull(response);
        assertEquals(1, response.weekNumber());
        assertEquals(2, response.activities().size());
        verify(llmGateway).classifyActivities(any(WeeklyLog.class));
        verify(activityRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando semana já estiver cadastrada no projeto")
    void shouldThrowConflictWhenWeekAlreadyExists() {
        CreateWeeklyLogRequest request = new CreateWeeklyLogRequest(
                projectId, 2, LocalDate.of(2026, 2, 8), LocalDate.of(2026, 2, 14), "Notas"
        );

        WeeklyLog existing = new WeeklyLog(UUID.randomUUID(), projectId, 2, LocalDate.of(2026, 2, 8), LocalDate.of(2026, 2, 14), "Já existe");

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(weeklyLogRepository.findByProjectIdAndWeekNumber(projectId, 2)).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> weeklyLogService.submitWeeklyLog(request));
        verify(weeklyLogRepository, never()).save(any());
    }
}
