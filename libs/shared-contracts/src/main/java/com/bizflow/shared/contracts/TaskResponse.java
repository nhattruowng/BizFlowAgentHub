package com.bizflow.shared.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String taskId;
    private TaskStatus status;
    private String workflowRunId;
    private Instant createdAt;
}
