package com.bizflow.approval.api;

import com.bizflow.shared.contracts.ApprovalStatus;

import java.time.Instant;

public class ApprovalResponse {
    private String id;
    private String workflowRunId;
    private ApprovalStatus status;
    private String requestedBy;
    private String decidedBy;
    private String reason;
    private Instant createdAt;

    public ApprovalResponse() {}

    public ApprovalResponse(String id, String workflowRunId, ApprovalStatus status, String requestedBy,
                            String decidedBy, String reason, Instant createdAt) {
        this.id = id;
        this.workflowRunId = workflowRunId;
        this.status = status;
        this.requestedBy = requestedBy;
        this.decidedBy = decidedBy;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
