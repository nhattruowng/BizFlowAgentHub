package com.bizflow.approval.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
    @NotBlank
    private String workflowRunId;

    @NotBlank
    private String requestedBy;

    @NotBlank
    private String reason;
}
