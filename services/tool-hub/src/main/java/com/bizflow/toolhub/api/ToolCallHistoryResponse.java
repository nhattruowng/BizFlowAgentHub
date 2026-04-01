package com.bizflow.toolhub.api;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
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
public class ToolCallHistoryResponse {
    private String callId;
    private String workflowRunId;
    private String toolName;
    private String status;
    private boolean approvalRequired;
    private ToolSideEffectLevel sideEffectLevel;
    private Map<String, Object> requestPayload;
    private Map<String, Object> responsePayload;
    private Instant createdAt;
}
