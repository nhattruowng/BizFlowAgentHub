package com.bizflow.approval.api;

import com.bizflow.shared.contracts.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {
    private String id;
    private String workflowRunId;
    private ApprovalStatus status;
    private String requestedBy;
    private String decidedBy;
    private String reason;
    private Instant createdAt;
}
