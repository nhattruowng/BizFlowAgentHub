package com.bizflow.shared.contracts;

public enum WorkflowStatus {
    CREATED,
    QUEUED,
    PLANNING,
    CONTEXT_LOADING,
    POLICY_CHECKING,
    WAITING_TOOL,
    VALIDATING,
    WAITING_APPROVAL,
    APPROVED,
    REJECTED,
    COMPLETED,
    FAILED,
    ESCALATED
}
