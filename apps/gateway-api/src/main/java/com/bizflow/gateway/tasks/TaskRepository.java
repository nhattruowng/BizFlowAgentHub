package com.bizflow.gateway.tasks;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, UUID> {
    Mono<TaskEntity> findByWorkflowRunId(String workflowRunId);
}
