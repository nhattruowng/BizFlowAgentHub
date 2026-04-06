package com.bizflow.gateway.tasks;

import com.bizflow.gateway.config.TenantIdentifierResolver;
import com.bizflow.gateway.workflow.WorkflowClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.bizflow.shared.contracts.TaskRequest;
import com.bizflow.shared.contracts.TaskResponse;
import com.bizflow.shared.contracts.TaskStatus;
import com.bizflow.shared.events.OutboxEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final TaskRepository taskRepository;
    private final TaskInputRepository taskInputRepository;
    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;
    private final TenantIdentifierResolver tenantIdentifierResolver;

    public Mono<TaskResponse> create(TaskRequest request) {
        Instant now = Instant.now();
        TaskEntity task = TaskEntity.builder()
                .tenantId(tenantIdentifierResolver.resolveRequired(request.getTenantId()))
                .source(request.getSource())
                .type(request.getType())
                .correlationId(resolveCorrelationId(request.getCorrelationId()))
                .status(TaskStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return taskRepository.save(task)
                .flatMap(savedTask -> persistTaskPayload(savedTask, request.getPayload())
                        .then(workflowClient.startWorkflow(buildWorkflowRequest(savedTask, request)))
                        .flatMap(runResponse -> markQueued(savedTask, runResponse))
                        .onErrorResume(ex -> markWorkflowFailure(savedTask, ex)));
    }

    public Mono<TaskResponse> get(UUID id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                .map(this::mapResponse);
    }

    public Mono<TaskDetailsResponse> getDetails(UUID id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                .flatMap(task -> taskInputRepository.findLatestByTaskId(task.getId())
                        .map(TaskInputEntity::getPayloadJson)
                        .defaultIfEmpty("{}")
                        .map(payload -> mapDetails(task, payload)));
    }

    public Flux<TaskResponse> list(String tenantId, int limit) {
        int normalizedLimit = Math.max(1, Math.min(limit, 100));
        String resolvedTenantId = StringUtils.hasText(tenantId)
                ? tenantIdentifierResolver.resolveRequired(tenantId)
                : null;

        return taskRepository.findAll()
                .filter(task -> resolvedTenantId == null || resolvedTenantId.equals(task.getTenantId()))
                .sort(Comparator.comparing(TaskEntity::getCreatedAt).reversed())
                .take(normalizedLimit)
                .map(this::mapResponse);
    }

    private Mono<TaskInputEntity> persistTaskPayload(TaskEntity task, Map<String, Object> payload) {
        return taskInputRepository.save(TaskInputEntity.builder()
                .taskId(task.getId())
                .payloadJson(toJson(payload))
                .createdAt(Instant.now())
                .build());
    }

    private WorkflowClient.WorkflowRunRequest buildWorkflowRequest(TaskEntity task, TaskRequest request) {
        return WorkflowClient.WorkflowRunRequest.builder()
                .workflowName(resolveWorkflowName(task.getType()))
                .tenantId(task.getTenantId())
                .taskId(task.getId().toString())
                .correlationId(task.getCorrelationId())
                .input(request.getPayload())
                .build();
    }

    private Mono<TaskResponse> markQueued(TaskEntity task, WorkflowClient.WorkflowRunResponse runResponse) {
        TaskEntity queuedTask = task.toBuilder()
                .workflowRunId(runResponse.getRunId())
                .status(mapWorkflowStatusToTaskStatus(runResponse.getStatus()))
                .updatedAt(Instant.now())
                .build();
        return taskRepository.save(queuedTask).map(this::mapResponse);
    }

    public Mono<Void> syncFromWorkflowEvent(OutboxEvent event) {
        if (event == null || event.getPayload() == null) {
            return Mono.empty();
        }
        String workflowRunId = valueAsString(event.getPayload(), "workflowRunId");
        String taskId = valueAsString(event.getPayload(), "taskId");
        String status = valueAsString(event.getPayload(), "status");

        Mono<TaskEntity> taskMono = Mono.empty();
        if (StringUtils.hasText(taskId)) {
            try {
                taskMono = taskRepository.findById(UUID.fromString(taskId));
            } catch (IllegalArgumentException ignored) {
                taskMono = Mono.empty();
            }
        }
        if (StringUtils.hasText(workflowRunId)) {
            taskMono = taskMono.switchIfEmpty(taskRepository.findByWorkflowRunId(workflowRunId));
        }

        return taskMono
                .flatMap(task -> taskRepository.save(task.toBuilder()
                        .workflowRunId(StringUtils.hasText(workflowRunId) ? workflowRunId : task.getWorkflowRunId())
                        .status(mapWorkflowStatusToTaskStatus(status))
                        .updatedAt(Instant.now())
                        .build()))
                .then();
    }

    private Mono<TaskResponse> markWorkflowFailure(TaskEntity task, Throwable error) {
        TaskEntity failedTask = task.toBuilder()
                .status(TaskStatus.FAILED)
                .updatedAt(Instant.now())
                .build();
        return taskRepository.save(failedTask)
                .then(Mono.error(new IllegalStateException("Failed to start workflow", error)));
    }

    private String resolveWorkflowName(String type) {
        if ("invoice_approval".equals(type)) {
            return "invoice-approval-workflow";
        }
        if ("policy_lookup".equals(type)) {
            return "policy-lookup-workflow";
        }
        return "email-ticket-workflow";
    }

    private String resolveCorrelationId(String correlationId) {
        return StringUtils.hasText(correlationId) ? correlationId : UUID.randomUUID().toString();
    }

    private TaskStatus mapWorkflowStatusToTaskStatus(String workflowStatus) {
        if (!StringUtils.hasText(workflowStatus)) {
            return TaskStatus.RUNNING;
        }
        switch (workflowStatus) {
            case "CREATED":
            case "QUEUED":
                return TaskStatus.QUEUED;
            case "COMPLETED":
            case "APPROVED":
                return TaskStatus.COMPLETED;
            case "FAILED":
            case "REJECTED":
            case "ESCALATED":
                return TaskStatus.FAILED;
            default:
                return TaskStatus.RUNNING;
        }
    }

    private TaskResponse mapResponse(TaskEntity entity) {
        return TaskResponse.builder()
                .taskId(entity.getId().toString())
                .status(entity.getStatus())
                .workflowRunId(entity.getWorkflowRunId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private TaskDetailsResponse mapDetails(TaskEntity entity, String payloadJson) {
        return TaskDetailsResponse.builder()
                .taskId(entity.getId().toString())
                .tenantId(entity.getTenantId())
                .source(entity.getSource())
                .type(entity.getType())
                .workflowName(resolveWorkflowName(entity.getType()))
                .status(entity.getStatus())
                .workflowRunId(entity.getWorkflowRunId())
                .correlationId(entity.getCorrelationId())
                .payload(fromJson(payloadJson))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize task payload", e);
        }
    }

    private Map<String, Object> fromJson(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize task payload", e);
        }
    }

    private String valueAsString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
