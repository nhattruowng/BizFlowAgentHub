package com.bizflow.gateway.tasks;

import com.bizflow.gateway.workflow.WorkflowClient;
import com.bizflow.shared.contracts.TaskRequest;
import com.bizflow.shared.contracts.TaskResponse;
import com.bizflow.shared.contracts.TaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskInputRepository taskInputRepository;
    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;

    public TaskService(TaskRepository taskRepository,
                       TaskInputRepository taskInputRepository,
                       WorkflowClient workflowClient,
                       ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.taskInputRepository = taskInputRepository;
        this.workflowClient = workflowClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        TaskEntity entity = new TaskEntity();
        entity.setTenantId(request.getTenantId());
        entity.setSource(request.getSource());
        entity.setType(request.getType());
        entity.setCorrelationId(request.getCorrelationId() == null ? UUID.randomUUID().toString() : request.getCorrelationId());
        entity.setStatus(TaskStatus.CREATED);
        entity = taskRepository.save(entity);

        TaskInputEntity input = new TaskInputEntity();
        input.setTaskId(entity.getId());
        input.setPayloadJson(toJson(request.getPayload()));
        taskInputRepository.save(input);

        WorkflowClient.WorkflowRunRequest runRequest = new WorkflowClient.WorkflowRunRequest();
        runRequest.setWorkflowName(resolveWorkflowName(entity.getType()));
        runRequest.setTenantId(entity.getTenantId());
        runRequest.setTaskId(entity.getId().toString());
        runRequest.setCorrelationId(entity.getCorrelationId());
        runRequest.setInput(request.getPayload());

        WorkflowClient.WorkflowRunResponse runResponse = workflowClient.startWorkflow(runRequest);
        if (runResponse != null) {
            entity.setWorkflowRunId(runResponse.getRunId());
            entity.setStatus(TaskStatus.QUEUED);
            taskRepository.save(entity);
        }

        return new TaskResponse(entity.getId().toString(), entity.getStatus(), entity.getWorkflowRunId(), Instant.now());
    }

    public TaskResponse get(UUID id) {
        TaskEntity entity = taskRepository.findById(id).orElseThrow();
        return new TaskResponse(entity.getId().toString(), entity.getStatus(), entity.getWorkflowRunId(), entity.getCreatedAt());
    }

    private String resolveWorkflowName(String type) {
        return switch (type) {
            case "invoice_approval" -> "invoice-approval-workflow";
            case "policy_lookup" -> "policy-lookup-workflow";
            default -> "email-ticket-workflow";
        };
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize task payload", e);
        }
    }
}
