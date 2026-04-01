package com.bizflow.gateway.tasks;

import com.bizflow.shared.contracts.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailsResponse {
    private String taskId;
    private String tenantId;
    private String source;
    private String type;
    private String workflowName;
    private TaskStatus status;
    private String workflowRunId;
    private String correlationId;
    private Map<String, Object> payload;
    private Instant createdAt;
    private Instant updatedAt;
}
