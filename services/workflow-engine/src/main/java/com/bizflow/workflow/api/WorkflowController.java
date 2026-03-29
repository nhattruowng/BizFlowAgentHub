package com.bizflow.workflow.api;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.workflow.engine.WorkflowEngineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    private final WorkflowEngineService engineService;

    public WorkflowController(WorkflowEngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping("/run")
    public ResponseEntity<WorkflowRunResponse> run(@Valid @RequestBody WorkflowRunRequest request) {
        return ResponseEntity.ok(engineService.start(request));
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<WorkflowRunResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(engineService.get(id));
    }
}
