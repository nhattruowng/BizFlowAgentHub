package com.bizflow.audit.core;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public List<AuditLogEntity> findByRunId(String runId) {
        return repository.findByWorkflowRunId(runId);
    }
}
