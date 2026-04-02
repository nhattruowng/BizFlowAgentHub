package com.bizflow.workflow.events;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OutboxEventRepository extends ReactiveCrudRepository<OutboxEventEntity, UUID> {
    Flux<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(String status);
}
