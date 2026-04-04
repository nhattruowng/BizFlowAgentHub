package com.bizflow.workflow.events;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.shared.contracts.WorkflowStepView;
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
    public static final String EVENT_TYPE_WORKFLOW_RUN_UPDATED = "workflow.run.updated";
    public static final String AGGREGATE_TYPE_WORKFLOW_RUN = "workflow_run";
    public static final String PRODUCER_SERVICE = "workflow-engine";
    public static final String STATUS_NEW = "NEW";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Mono<Void> appendWorkflowRunCreatedEvent(WorkflowRunResponse response, WorkflowRunRequest request) {
        return appendEvent(response.getRunId(), EVENT_TYPE_WORKFLOW_RUN_CREATED, buildCreatedPayload(response, request));
    }

    public Mono<Void> appendWorkflowRunUpdatedEvent(WorkflowRunResponse response, Map<String, Object> metadata) {
        Map<String, Object> payload = buildWorkflowPayload(response);
        if (metadata != null && !metadata.isEmpty()) {
            payload.putAll(metadata);
        }
        return appendEvent(response.getRunId(), EVENT_TYPE_WORKFLOW_RUN_UPDATED, payload);
    }

    public Mono<Void> appendEvent(String aggregateId, String eventType, Map<String, Object> payload) {
        return outboxEventRepository.save(OutboxEventEntity.builder()
                        .producerService(PRODUCER_SERVICE)
                        .aggregateType(AGGREGATE_TYPE_WORKFLOW_RUN)
                        .aggregateId(aggregateId)
                        .eventType(eventType)
                        .payload(serializePayload(payload))
                        .status(STATUS_NEW)
                        .createdAt(Instant.now())
                        .build())
                .then();
    }

    private Map<String, Object> buildCreatedPayload(WorkflowRunResponse response, WorkflowRunRequest request) {
        Map<String, Object> payload = buildWorkflowPayload(response);
        payload.put("tenantId", request.getTenantId());
        payload.put("taskId", request.getTaskId());
        return payload;
    }

    private Map<String, Object> buildWorkflowPayload(WorkflowRunResponse response) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("workflowRunId", response.getRunId());
        payload.put("workflowName", response.getWorkflowName());
        payload.put("correlationId", response.getCorrelationId());
        payload.put("status", response.getStatus());
        payload.put("currentStep", response.getCurrentStep());
        payload.put("steps", response.getSteps());
        payload.put("stepDetails", response.getStepDetails() == null
                ? null
                : response.getStepDetails().stream().map(this::toStepPayload).toList());
        payload.put("startedAt", response.getStartedAt());
        payload.put("updatedAt", response.getUpdatedAt());
        return payload;
    }

    private Map<String, Object> toStepPayload(WorkflowStepView stepView) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("stepName", stepView.getStepName());
        payload.put("status", stepView.getStatus());
        payload.put("attempt", stepView.getAttempt());
        payload.put("startedAt", stepView.getStartedAt());
        payload.put("endedAt", stepView.getEndedAt());
        payload.put("errorReason", stepView.getErrorReason());
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
