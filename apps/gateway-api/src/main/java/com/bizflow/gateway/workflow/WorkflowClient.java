package com.bizflow.gateway.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class WorkflowClient {
    private final WebClient webClient;

    public WorkflowClient(WebClient.Builder webClientBuilder,
                          @Value("${workflow.engine.url:http://localhost:8082}") String workflowEngineUrl) {
        this.webClient = webClientBuilder.baseUrl(workflowEngineUrl).build();
    }

    public Mono<WorkflowRunResponse> startWorkflow(WorkflowRunRequest request) {
        return webClient.post()
                .uri("/api/workflows/run")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WorkflowRunResponse.class);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkflowRunRequest {
        private String workflowName;
        private String tenantId;
        private String taskId;
        private String correlationId;
        private Map<String, Object> input;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowRunResponse {
        private String runId;
        private String status;
    }
}
