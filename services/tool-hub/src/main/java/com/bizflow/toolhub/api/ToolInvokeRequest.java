package com.bizflow.toolhub.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolInvokeRequest {
    private String workflowRunId;
    private Map<String, Object> input;
}
