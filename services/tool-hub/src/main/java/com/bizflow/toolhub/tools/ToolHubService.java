package com.bizflow.toolhub.tools;

import com.bizflow.toolhub.api.ToolCallHistoryResponse;
import com.bizflow.toolhub.api.ToolInvokeRequest;
import com.bizflow.toolhub.api.ToolInvokeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ToolHubService {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ToolRepository toolRepository;
    private final ToolCallRepository toolCallRepository;
    private final ObjectMapper objectMapper;

    public Flux<ToolEntity> listTools() {
        return toolRepository.findAll()
                .sort(Comparator.comparing(ToolEntity::getToolName));
    }

    public Flux<ToolCallHistoryResponse> listCalls(String workflowRunId) {
        return toolCallRepository.findByWorkflowRunId(workflowRunId)
                .flatMap(call -> toolRepository.findById(call.getToolId())
                        .map(tool -> mapHistory(call, tool)));
    }

    public Mono<ToolInvokeResponse> invoke(String toolName, ToolInvokeRequest request) {
        return toolRepository.findByToolName(toolName)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool not found: " + toolName)))
                .flatMap(tool -> {
                    validateInput(toolName, request.getInput());

                    Map<String, Object> output = tool.isApprovalRequired()
                            ? approvalOutput(toolName, request.getInput())
                            : executeMock(toolName, request.getInput());
                    String status = tool.isApprovalRequired() ? "WAITING_APPROVAL" : "COMPLETED";

                    ToolCallEntity call = ToolCallEntity.builder()
                            .toolId(tool.getId())
                            .workflowRunId(request.getWorkflowRunId())
                            .requestPayload(toJson(request.getInput()))
                            .responsePayload(toJson(output))
                            .status(status)
                            .createdAt(Instant.now())
                            .build();

                    return toolCallRepository.save(call)
                            .map(savedCall -> ToolInvokeResponse.builder()
                                    .callId(savedCall.getId().toString())
                                    .toolName(toolName)
                                    .status(status)
                                    .output(output)
                                    .sideEffectLevel(tool.getSideEffectLevel())
                                    .approvalRequired(tool.isApprovalRequired())
                                    .build());
                });
    }

    private void validateInput(String toolName, Map<String, Object> input) {
        List<String> requiredKeys = requiredKeys(toolName);
        List<String> missingKeys = requiredKeys.stream()
                .filter(key -> input == null || !input.containsKey(key))
                .toList();
        if (!missingKeys.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing required input for " + toolName + ": " + String.join(", ", missingKeys)
            );
        }
    }

    private List<String> requiredKeys(String toolName) {
        if ("send_email_draft".equals(toolName)) {
            return List.of("subject", "body");
        }
        if ("create_ticket".equals(toolName)) {
            return List.of("title", "description");
        }
        if ("read_policy_docs".equals(toolName)) {
            return List.of("query");
        }
        if ("db_query_readonly".equals(toolName)) {
            return List.of("sql");
        }
        if ("extract_invoice_mock".equals(toolName)) {
            return List.of("invoiceId");
        }
        if ("submit_approval_request".equals(toolName)) {
            return List.of("reason");
        }
        if ("notify_user".equals(toolName)) {
            return List.of("message");
        }
        return List.of();
    }

    private Map<String, Object> executeMock(String toolName, Map<String, Object> input) {
        Map<String, Object> output = new HashMap<>();
        output.put("toolName", toolName);
        if ("send_email_draft".equals(toolName)) {
            output.put("draftSubject", input.get("subject"));
            output.put("draftBody", input.get("body"));
            output.put("deliveryMode", "DRAFT_ONLY");
            return output;
        }
        if ("create_ticket".equals(toolName)) {
            output.put("ticketId", "TCK-" + Math.abs(input.hashCode()));
            output.put("queued", true);
            return output;
        }
        if ("read_policy_docs".equals(toolName)) {
            output.put("matches", List.of("Invoice Policy", "Support SLA"));
            output.put("query", input.get("query"));
            return output;
        }
        if ("db_query_readonly".equals(toolName)) {
            output.put("sql", input.get("sql"));
            output.put("rows", 1);
            output.put("executionMode", "READ_ONLY_MOCK");
            return output;
        }
        if ("extract_invoice_mock".equals(toolName)) {
            output.put("invoiceId", input.get("invoiceId"));
            output.put("amount", 1500);
            output.put("currency", "USD");
            return output;
        }
        if ("notify_user".equals(toolName)) {
            output.put("notified", true);
            output.put("message", input.get("message"));
            return output;
        }
        output.put("echo", input);
        return output;
    }

    private Map<String, Object> approvalOutput(String toolName, Map<String, Object> input) {
        Map<String, Object> output = new HashMap<>();
        output.put("toolName", toolName);
        output.put("message", "Approval required before execution");
        output.put("approvalContext", input);
        return output;
    }

    private ToolCallHistoryResponse mapHistory(ToolCallEntity call, ToolEntity tool) {
        return ToolCallHistoryResponse.builder()
                .callId(call.getId().toString())
                .workflowRunId(call.getWorkflowRunId())
                .toolName(tool.getToolName())
                .status(call.getStatus())
                .approvalRequired(tool.isApprovalRequired())
                .sideEffectLevel(tool.getSideEffectLevel())
                .requestPayload(fromJson(call.getRequestPayload()))
                .responsePayload(fromJson(call.getResponsePayload()))
                .createdAt(call.getCreatedAt())
                .build();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private Map<String, Object> fromJson(String payload) {
        try {
            return objectMapper.readValue(payload == null ? "{}" : payload, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize tool payload", e);
        }
    }
}
