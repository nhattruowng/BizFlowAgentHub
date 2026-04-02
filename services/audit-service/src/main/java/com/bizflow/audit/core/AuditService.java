package com.bizflow.audit.core;

import com.bizflow.audit.api.AuditLogRequest;
import com.bizflow.shared.events.OutboxEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public Mono<AuditLogEntity> append(AuditLogRequest request) {
        return repository.save(AuditLogEntity.builder()
                .workflowRunId(request.getWorkflowRunId())
                .action(request.getAction())
                .payload(request.getPayload())
                .createdAt(Instant.now())
                .build());
    }

    public Mono<AuditLogEntity> appendFromEvent(OutboxEvent event) {
        if (event == null || !StringUtils.hasText(event.getId())) {
            return Mono.empty();
        }
        return repository.findBySourceEventId(event.getId())
                .switchIfEmpty(Mono.defer(() -> repository.save(AuditLogEntity.builder()
                        .workflowRunId(event.getAggregateId())
                        .action(toAuditAction(event.getEventType()))
                        .payload(serializeEvent(event))
                        .sourceEventId(event.getId())
                        .createdAt(event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now())
                        .build())));
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

    private String toAuditAction(String eventType) {
        if (!StringUtils.hasText(eventType)) {
            return "EVENT_RECEIVED";
        }
        return eventType.replace('.', '_').toUpperCase();
    }

    private String serializeEvent(OutboxEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize consumed event", exception);
        }
    }
}
