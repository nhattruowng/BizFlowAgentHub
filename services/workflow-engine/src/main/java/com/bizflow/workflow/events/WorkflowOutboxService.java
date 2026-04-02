package com.bizflow.workflow.events;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.workflow.api.WorkflowRunRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowOutboxService {
    public static final String EVENT_TYPE_WORKFLOW_RUN_CREATED = "workflow.run.created";
    public static final String AGGREGATE_TYPE_WORKFLOW_RUN = "workflow_run";
    public static final String STATUS_NEW = "NEW";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Mono<Void> appendWorkflowRunCreatedEvent(WorkflowRunResponse response, WorkflowRunRequest request) {
        return outboxEventRepository.save(OutboxEventEntity.builder()
                        .aggregateType(AGGREGATE_TYPE_WORKFLOW_RUN)
                        .aggregateId(response.getRunId())
                        .eventType(EVENT_TYPE_WORKFLOW_RUN_CREATED)
                        .payload(serializePayload(buildPayload(response, request)))
                        .status(STATUS_NEW)
                        .createdAt(Instant.now())
                        .build())
                .then();
    }

    private Map<String, Object> buildPayload(WorkflowRunResponse response, WorkflowRunRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("workflowRunId", response.getRunId());
        payload.put("workflowName", response.getWorkflowName());
        payload.put("tenantId", request.getTenantId());
        payload.put("taskId", request.getTaskId());
        payload.put("correlationId", response.getCorrelationId());
        payload.put("status", response.getStatus());
        payload.put("currentStep", response.getCurrentStep());
        payload.put("steps", response.getSteps());
        payload.put("startedAt", response.getStartedAt());
        payload.put("updatedAt", response.getUpdatedAt());
        return payload;
    }

    private String serializePayload(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize workflow outbox payload", exception);
        }
    }
}
