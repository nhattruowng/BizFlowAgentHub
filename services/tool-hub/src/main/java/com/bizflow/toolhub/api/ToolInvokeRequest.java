package com.bizflow.toolhub.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String workflowRunId;

    @NotNull
    private Map<String, Object> input;
}
