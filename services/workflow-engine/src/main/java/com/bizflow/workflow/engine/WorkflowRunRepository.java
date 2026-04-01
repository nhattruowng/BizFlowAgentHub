package com.bizflow.workflow.engine;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface WorkflowRunRepository extends ReactiveCrudRepository<WorkflowRunEntity, UUID> {
}
