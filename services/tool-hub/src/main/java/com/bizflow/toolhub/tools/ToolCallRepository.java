package com.bizflow.toolhub.tools;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ToolCallRepository extends ReactiveCrudRepository<ToolCallEntity, UUID> {
    Flux<ToolCallEntity> findByWorkflowRunId(String workflowRunId);
}
