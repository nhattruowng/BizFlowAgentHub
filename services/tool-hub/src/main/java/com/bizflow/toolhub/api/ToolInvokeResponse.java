package com.bizflow.toolhub.api;

import java.util.Map;

public class ToolInvokeResponse {
    private String toolName;
    private String status;
    private Map<String, Object> output;
    private boolean approvalRequired;

    public ToolInvokeResponse() {}

    public ToolInvokeResponse(String toolName, String status, Map<String, Object> output, boolean approvalRequired) {
        this.toolName = toolName;
        this.status = status;
        this.output = output;
        this.approvalRequired = approvalRequired;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }
}
