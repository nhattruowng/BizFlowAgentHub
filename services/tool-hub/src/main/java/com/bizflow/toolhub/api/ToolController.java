package com.bizflow.toolhub.api;

import com.bizflow.toolhub.tools.ToolEntity;
import com.bizflow.toolhub.tools.ToolHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {
    private final ToolHubService toolHubService;

    @GetMapping
    public Flux<ToolEntity> list() {
        return toolHubService.listTools();
    }

    @GetMapping("/calls/{workflowRunId}")
    public Flux<ToolCallHistoryResponse> listCalls(@PathVariable String workflowRunId) {
        return toolHubService.listCalls(workflowRunId);
    }

    @PostMapping("/{toolName}/invoke")
    public Mono<ToolInvokeResponse> invoke(@PathVariable String toolName,
                                           @Valid @RequestBody ToolInvokeRequest request) {
        return toolHubService.invoke(toolName, request);
    }
}
