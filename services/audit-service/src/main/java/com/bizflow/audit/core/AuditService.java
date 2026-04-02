package com.bizflow.audit.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository repository;

    public Flux<AuditLogEntity> findByRunId(String runId) {
        return repository.findByWorkflowRunId(runId);
    }
}
