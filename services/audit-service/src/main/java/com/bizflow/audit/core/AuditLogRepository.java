package com.bizflow.audit.core;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AuditLogRepository extends ReactiveCrudRepository<AuditLogEntity, UUID> {
    Flux<AuditLogEntity> findByWorkflowRunId(String workflowRunId);
}
