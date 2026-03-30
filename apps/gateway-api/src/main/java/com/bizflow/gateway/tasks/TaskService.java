package com.bizflow.gateway.tasks;

import com.bizflow.gateway.workflow.WorkflowClient;
import com.bizflow.shared.contracts.TaskRequest;
import com.bizflow.shared.contracts.TaskResponse;
import com.bizflow.shared.contracts.TaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskInputRepository taskInputRepository;
    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;

    public Mono<TaskResponse> create(TaskRequest request) {
        Instant now = Instant.now();
        TaskEntity task = TaskEntity.builder()
                .tenantId(request.getTenantId())
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
                .status(TaskStatus.QUEUED)
                .updatedAt(Instant.now())
                .build();
        return taskRepository.save(queuedTask).map(this::mapResponse);
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
        return switch (type) {
            case "invoice_approval" -> "invoice-approval-workflow";
            case "policy_lookup" -> "policy-lookup-workflow";
            default -> "email-ticket-workflow";
        };
    }

    private String resolveCorrelationId(String correlationId) {
        return StringUtils.hasText(correlationId) ? correlationId : UUID.randomUUID().toString();
    }

    private TaskResponse mapResponse(TaskEntity entity) {
        return TaskResponse.builder()
                .taskId(entity.getId().toString())
                .status(entity.getStatus())
                .workflowRunId(entity.getWorkflowRunId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize task payload", e);
        }
    }
}
