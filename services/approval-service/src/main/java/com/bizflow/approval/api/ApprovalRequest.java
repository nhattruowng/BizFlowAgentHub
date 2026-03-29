package com.bizflow.approval.api;

import jakarta.validation.constraints.NotBlank;

public class ApprovalRequest {
    @NotBlank
    private String workflowRunId;
    @NotBlank
    private String requestedBy;
    @NotBlank
    private String reason;

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
