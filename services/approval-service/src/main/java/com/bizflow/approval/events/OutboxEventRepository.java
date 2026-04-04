package com.bizflow.approval.events;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OutboxEventRepository extends ReactiveCrudRepository<OutboxEventEntity, UUID> {
    Flux<OutboxEventEntity> findByProducerServiceAndStatusOrderByCreatedAtAsc(String producerService, String status);
}
