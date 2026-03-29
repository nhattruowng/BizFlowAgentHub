package com.bizflow.gateway.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WorkflowClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${workflow.engine.url:http://localhost:8082}")
    private String workflowEngineUrl;

    public WorkflowRunResponse startWorkflow(WorkflowRunRequest request) {
        return restTemplate.postForObject(workflowEngineUrl + "/api/workflows/run", request, WorkflowRunResponse.class);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkflowRunRequest {
        private String workflowName;
        private String tenantId;
        private String taskId;
        private String correlationId;
        private Map<String, Object> input;

        public String getWorkflowName() {
            return workflowName;
        }

        public void setWorkflowName(String workflowName) {
            this.workflowName = workflowName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public Map<String, Object> getInput() {
            return input;
        }

        public void setInput(Map<String, Object> input) {
            this.input = input;
        }
    }

    public static class WorkflowRunResponse {
        private String runId;
        private String status;

        public String getRunId() {
            return runId;
        }

        public void setRunId(String runId) {
            this.runId = runId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
