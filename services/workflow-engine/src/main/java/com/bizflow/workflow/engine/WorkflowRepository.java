package com.bizflow.workflow.engine;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WorkflowRepository extends ReactiveCrudRepository<WorkflowEntity, UUID> {
    Mono<WorkflowEntity> findByName(String name);
}
