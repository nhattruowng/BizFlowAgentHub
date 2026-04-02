package com.bizflow.audit.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {
    @NotBlank
    private String workflowRunId;

    @NotBlank
    private String action;

    private String payload;
}
