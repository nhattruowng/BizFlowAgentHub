package com.bizflow.shared.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRunResponse {
    private String runId;
    private String workflowName;
    private WorkflowStatus status;
    private String correlationId;
    private Instant startedAt;
    private Instant updatedAt;
    private List<String> steps;
}
