package com.bizflow.audit.core;

import com.bizflow.audit.api.AuditLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository repository;

    public Mono<AuditLogEntity> append(AuditLogRequest request) {
        return repository.save(AuditLogEntity.builder()
                .workflowRunId(request.getWorkflowRunId())
                .action(request.getAction())
                .payload(request.getPayload())
                .createdAt(Instant.now())
                .build());
    }

    public Flux<AuditLogEntity> list(String workflowRunId, String action) {
        Flux<AuditLogEntity> logs;
        if (StringUtils.hasText(workflowRunId) && StringUtils.hasText(action)) {
            logs = repository.findByWorkflowRunIdAndAction(workflowRunId, action);
        } else if (StringUtils.hasText(workflowRunId)) {
            logs = repository.findByWorkflowRunId(workflowRunId);
        } else if (StringUtils.hasText(action)) {
            logs = repository.findByAction(action);
        } else {
            logs = repository.findAll();
        }
        return logs.sort(Comparator.comparing(AuditLogEntity::getCreatedAt).reversed());
    }
}
