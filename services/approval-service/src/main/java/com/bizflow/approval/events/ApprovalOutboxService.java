package com.bizflow.approval.events;

import com.bizflow.approval.api.ApprovalResponse;
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
public class ApprovalOutboxService {
    public static final String EVENT_TYPE_APPROVAL_REQUESTED = "approval.requested";
    public static final String EVENT_TYPE_APPROVAL_APPROVED = "approval.approved";
    public static final String EVENT_TYPE_APPROVAL_REJECTED = "approval.rejected";
    public static final String AGGREGATE_TYPE_APPROVAL = "approval";
    public static final String PRODUCER_SERVICE = "approval-service";
    public static final String STATUS_NEW = "NEW";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Mono<Void> appendApprovalRequestedEvent(ApprovalResponse response) {
        return appendEvent(response, EVENT_TYPE_APPROVAL_REQUESTED);
    }

    public Mono<Void> appendApprovalDecisionEvent(ApprovalResponse response) {
        String eventType = "APPROVED".equals(String.valueOf(response.getStatus()))
                ? EVENT_TYPE_APPROVAL_APPROVED
                : EVENT_TYPE_APPROVAL_REJECTED;
        return appendEvent(response, eventType);
    }

    private Mono<Void> appendEvent(ApprovalResponse response, String eventType) {
        return outboxEventRepository.save(OutboxEventEntity.builder()
                        .producerService(PRODUCER_SERVICE)
                        .aggregateType(AGGREGATE_TYPE_APPROVAL)
                        .aggregateId(response.getId())
                        .eventType(eventType)
                        .payload(serializePayload(buildPayload(response)))
                        .status(STATUS_NEW)
                        .createdAt(Instant.now())
                        .build())
                .then();
    }

    private Map<String, Object> buildPayload(ApprovalResponse response) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("approvalId", response.getId());
        payload.put("workflowRunId", response.getWorkflowRunId());
        payload.put("status", response.getStatus());
        payload.put("requestedBy", response.getRequestedBy());
        payload.put("decidedBy", response.getDecidedBy());
        payload.put("reason", response.getReason());
        payload.put("createdAt", response.getCreatedAt());
        payload.put("updatedAt", response.getUpdatedAt());
        return payload;
    }

    private String serializePayload(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize approval outbox payload", exception);
        }
    }
}
