package com.bizflow.workflow.engine;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface WorkflowStepRepository extends ReactiveCrudRepository<WorkflowStepEntity, UUID> {
    Flux<WorkflowStepEntity> findByRunId(UUID runId);
}
