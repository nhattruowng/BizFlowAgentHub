package com.bizflow.toolhub.tools;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
import com.bizflow.toolhub.api.ApprovalClient;
import com.bizflow.toolhub.api.ToolInvokeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToolHubServiceTest {
    @Mock
    private ToolRepository toolRepository;

    @Mock
    private ToolCallRepository toolCallRepository;

    @Mock
    private ApprovalClient approvalClient;

    private ToolHubService toolHubService;

    @BeforeEach
    void setUp() {
        toolHubService = new ToolHubService(toolRepository, toolCallRepository, approvalClient, new ObjectMapper());
    }

    @Test
    void invokeRejectsMissingRequiredInput() {
        ToolEntity tool = ToolEntity.builder()
                .id(UUID.randomUUID())
                .toolName("create_ticket")
                .sideEffectLevel(ToolSideEffectLevel.WRITE)
                .approvalRequired(false)
                .build();
        when(toolRepository.findByToolName("create_ticket")).thenReturn(Mono.just(tool));

        ToolInvokeRequest request = ToolInvokeRequest.builder()
                .workflowRunId("run-1")
                .input(Map.of("title", "Bug"))
                .build();

        assertThatThrownBy(() -> toolHubService.invoke("create_ticket", request).block())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("description");
    }

    @Test
    void invokeApprovalToolReturnsWaitingApproval() {
        UUID toolId = UUID.randomUUID();
        UUID callId = UUID.randomUUID();
        ToolEntity tool = ToolEntity.builder()
                .id(toolId)
                .toolName("submit_approval_request")
                .sideEffectLevel(ToolSideEffectLevel.WRITE)
                .approvalRequired(true)
                .build();
        ToolCallEntity savedCall = ToolCallEntity.builder()
                .id(callId)
                .toolId(toolId)
                .workflowRunId("run-2")
                .status("WAITING_APPROVAL")
                .requestPayload("{\"reason\":\"Need approval\"}")
                .responsePayload("{\"message\":\"Approval required before execution\",\"approvalId\":\"approval-123\",\"approvalStatus\":\"PENDING\"}")
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();

        when(toolRepository.findByToolName("submit_approval_request")).thenReturn(Mono.just(tool));
        when(approvalClient.createApproval(any())).thenReturn(Mono.just(ApprovalClient.ApprovalResponse.builder()
                .id("approval-123")
                .workflowRunId("run-2")
                .status("PENDING")
                .build()));
        when(toolCallRepository.save(any(ToolCallEntity.class))).thenReturn(Mono.just(savedCall));

        ToolInvokeRequest request = ToolInvokeRequest.builder()
                .workflowRunId("run-2")
                .input(Map.of("reason", "Need approval"))
                .build();

        var response = toolHubService.invoke("submit_approval_request", request).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("WAITING_APPROVAL");
        assertThat(response.isApprovalRequired()).isTrue();
        assertThat(response.getCallId()).isEqualTo(callId.toString());
        assertThat(response.getOutput()).containsEntry("approvalId", "approval-123");
    }

    @Test
    void listCallsResolvesToolMetadata() {
        UUID toolId = UUID.randomUUID();
        UUID callId = UUID.randomUUID();
        ToolCallEntity call = ToolCallEntity.builder()
                .id(callId)
                .toolId(toolId)
                .workflowRunId("run-3")
                .status("COMPLETED")
                .requestPayload("{\"query\":\"invoice\"}")
                .responsePayload("{\"matches\":[\"Invoice Policy\"]}")
                .createdAt(Instant.parse("2026-04-02T03:10:00Z"))
                .build();
        ToolEntity tool = ToolEntity.builder()
                .id(toolId)
                .toolName("read_policy_docs")
                .sideEffectLevel(ToolSideEffectLevel.READ)
                .approvalRequired(false)
                .build();

        when(toolCallRepository.findByWorkflowRunId("run-3")).thenReturn(Flux.just(call));
        when(toolRepository.findById(toolId)).thenReturn(Mono.just(tool));

        var responses = toolHubService.listCalls("run-3").collectList().block();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getToolName()).isEqualTo("read_policy_docs");
        assertThat(responses.getFirst().getRequestPayload()).containsEntry("query", "invoice");
    }
}
