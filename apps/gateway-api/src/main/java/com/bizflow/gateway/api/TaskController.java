package com.bizflow.gateway.api;

import com.bizflow.gateway.tasks.TaskService;
import com.bizflow.shared.contracts.TaskRequest;
import com.bizflow.shared.contracts.TaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public Mono<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public Mono<TaskResponse> get(@PathVariable UUID id) {
        return taskService.get(id);
    }
}
