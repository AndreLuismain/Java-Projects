package br.com.andreluismain.pix.integration.event;

import br.com.andreluismain.pix.domain.model.OutboxEvent;
import br.com.andreluismain.pix.domain.model.OutboxStatus;
import br.com.andreluismain.pix.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Publicador assíncrono para o padrão Transactional Outbox.
 * Varre eventos pendentes e simula a publicação para o broker de mensageria com retry.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    /**
     * Processa e publica eventos de outbox pendentes em lote.
     * Pode ser disparado agendado ou manualmente.
     */
    @Transactional
    public int publishPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : pending) {
            try {
                event.incrementAttempt();
                // Simulação de despacho para tópico de mensageria (Kafka/RabbitMQ)
                log.info("Disparando evento outbox: id={}, tipo={}, aggregateId={}",
                        event.getId(), event.getEventType(), event.getAggregateId());
                event.markPublished();
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Falha ao publicar evento outbox: id={}", event.getId(), e);
            }
        }
        return pending.size();
    }
}
