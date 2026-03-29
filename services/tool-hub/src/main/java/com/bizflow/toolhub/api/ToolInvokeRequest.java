package com.bizflow.toolhub.api;

import java.util.Map;

public class ToolInvokeRequest {
    private String workflowRunId;
    private Map<String, Object> input;

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }
}
