package com.bizflow.toolhub.tools;

import com.bizflow.toolhub.api.ToolInvokeRequest;
import com.bizflow.toolhub.api.ToolInvokeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ToolHubService {
    private final ToolRepository toolRepository;
    private final ToolCallRepository toolCallRepository;
    private final ObjectMapper objectMapper;

    public Flux<ToolEntity> listTools() {
        return toolRepository.findAll();
    }

    public Mono<ToolInvokeResponse> invoke(String toolName, ToolInvokeRequest request) {
        return toolRepository.findByToolName(toolName)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool not found: " + toolName)))
                .flatMap(tool -> {
                    Map<String, Object> output = new HashMap<>();
                    String status;
                    if (tool.isApprovalRequired()) {
                        status = "WAITING_APPROVAL";
                        output.put("message", "Approval required for tool: " + toolName);
                    } else {
                        status = "COMPLETED";
                        output.put("message", "Mock execution for tool: " + toolName);
                        output.put("echo", request.getInput());
                    }

                    ToolCallEntity call = ToolCallEntity.builder()
                            .toolId(tool.getId())
                            .workflowRunId(request.getWorkflowRunId())
                            .requestPayload(toJson(request.getInput()))
                            .responsePayload(toJson(output))
                            .status(status)
                            .createdAt(Instant.now())
                            .build();

                    return toolCallRepository.save(call)
                            .thenReturn(ToolInvokeResponse.builder()
                                    .toolName(toolName)
                                    .status(status)
                                    .output(output)
                                    .approvalRequired(tool.isApprovalRequired())
                                    .build());
                });
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
