package com.bizflow.workflow.api;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.workflow.engine.WorkflowEngineService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowEngineService engineService;

    @GetMapping
    public Flux<WorkflowDefinitionResponse> listDefinitions() {
        return engineService.listDefinitions();
    }

    @PostMapping("/run")
    public Mono<WorkflowRunResponse> run(@Valid @RequestBody WorkflowRunRequest request) {
        return engineService.start(request);
    }

    @GetMapping("/runs/{id}")
    public Mono<WorkflowRunResponse> get(@PathVariable UUID id) {
        return engineService.get(id);
    }
}
