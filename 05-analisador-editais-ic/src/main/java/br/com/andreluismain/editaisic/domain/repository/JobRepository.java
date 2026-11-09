package br.com.andreluismain.editaisic.domain.repository;

import br.com.andreluismain.editaisic.domain.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para consulta e atualização de jobs em lote.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
}
