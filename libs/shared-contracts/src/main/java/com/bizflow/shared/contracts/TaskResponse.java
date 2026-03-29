package com.bizflow.shared.contracts;

import java.time.Instant;

public class TaskResponse {
    private String taskId;
    private TaskStatus status;
    private String workflowRunId;
    private Instant createdAt;

    public TaskResponse() {}

    public TaskResponse(String taskId, TaskStatus status, String workflowRunId, Instant createdAt) {
        this.taskId = taskId;
        this.status = status;
        this.workflowRunId = workflowRunId;
        this.createdAt = createdAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
