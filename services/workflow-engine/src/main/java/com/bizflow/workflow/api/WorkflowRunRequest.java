package com.bizflow.workflow.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRunRequest {
    @NotBlank
    private String workflowName;

    @NotBlank
    private String tenantId;

    @NotBlank
    private String taskId;

    private String correlationId;
    private Map<String, Object> input;
}
