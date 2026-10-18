package br.com.andreluismain.relatoriosic.domain.repository;

import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para consulta de logs semanais de pesquisa.
 */
@Repository
public interface WeeklyLogRepository extends JpaRepository<WeeklyLog, UUID> {

    List<WeeklyLog> findByProjectIdOrderByWeekNumberAsc(UUID projectId);

    Optional<WeeklyLog> findByProjectIdAndWeekNumber(UUID projectId, Integer weekNumber);
}
