package br.com.andreluismain.agendamento.repository;

import br.com.andreluismain.agendamento.domain.model.Resource;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para consulta e bloqueio pessimista de recursos acadêmicos.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Resource r WHERE r.id = :id")
    Optional<Resource> findByIdWithLock(@Param("id") UUID id);
}
