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
public class ToolInvokeResponse {
    private String toolName;
    private String status;
    private Map<String, Object> output;
    private boolean approvalRequired;
}
