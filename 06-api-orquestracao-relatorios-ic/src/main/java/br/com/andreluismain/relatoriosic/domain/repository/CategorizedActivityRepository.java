package br.com.andreluismain.relatoriosic.domain.repository;

import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório para consulta das atividades categorizadas por log semanal.
 */
@Repository
public interface CategorizedActivityRepository extends JpaRepository<CategorizedActivity, UUID> {

    List<CategorizedActivity> findByWeeklyLogId(UUID weeklyLogId);

    void deleteByWeeklyLogId(UUID weeklyLogId);
}
