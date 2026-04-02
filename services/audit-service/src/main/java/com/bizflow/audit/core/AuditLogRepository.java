package com.bizflow.audit.core;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuditLogRepository extends ReactiveCrudRepository<AuditLogEntity, UUID> {
    Flux<AuditLogEntity> findByWorkflowRunId(String workflowRunId);
    Flux<AuditLogEntity> findByAction(String action);
    Flux<AuditLogEntity> findByWorkflowRunIdAndAction(String workflowRunId, String action);
    Mono<AuditLogEntity> findBySourceEventId(String sourceEventId);
}
