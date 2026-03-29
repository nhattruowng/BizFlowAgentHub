package com.bizflow.toolhub.tools;

import com.bizflow.toolhub.api.ToolInvokeRequest;
import com.bizflow.toolhub.api.ToolInvokeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ToolHubService {
    private final ToolRepository toolRepository;
    private final ToolCallRepository toolCallRepository;
    private final ObjectMapper objectMapper;

    public ToolHubService(ToolRepository toolRepository, ToolCallRepository toolCallRepository, ObjectMapper objectMapper) {
        this.toolRepository = toolRepository;
        this.toolCallRepository = toolCallRepository;
        this.objectMapper = objectMapper;
    }

    public List<ToolEntity> listTools() {
        return toolRepository.findAll();
    }

    public ToolInvokeResponse invoke(String toolName, ToolInvokeRequest request) {
        ToolEntity tool = toolRepository.findByToolName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        ToolCallEntity call = new ToolCallEntity();
        call.setToolId(tool.getId());
        call.setWorkflowRunId(request.getWorkflowRunId());
        call.setRequestPayload(toJson(request.getInput()));

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

        call.setStatus(status);
        call.setResponsePayload(toJson(output));
        toolCallRepository.save(call);

        return new ToolInvokeResponse(toolName, status, output, tool.isApprovalRequired());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
