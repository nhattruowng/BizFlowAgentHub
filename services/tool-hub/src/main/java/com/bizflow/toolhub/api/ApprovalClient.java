package com.bizflow.toolhub.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApprovalClient {
    private final WebClient webClient;

    public ApprovalClient(WebClient.Builder webClientBuilder,
                          @Value("${approval.service.url:http://localhost:8084}") String approvalServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(approvalServiceUrl).build();
    }

    public Mono<ApprovalResponse> createApproval(ApprovalCreateRequest request) {
        return webClient.post()
                .uri("/api/approvals")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApprovalResponse.class);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApprovalCreateRequest {
        private String workflowRunId;
        private String requestedBy;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalResponse {
        private String id;
        private String workflowRunId;
        private String status;
    }
}
