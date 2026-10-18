package br.com.andreluismain.pix.repository;

import br.com.andreluismain.pix.domain.model.OutboxEvent;
import br.com.andreluismain.pix.domain.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório para persistência e polling de eventos outbox.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
