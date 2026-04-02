package com.bizflow.approval.api;

import com.bizflow.approval.core.ApprovalService;
import com.bizflow.shared.contracts.ApprovalStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    private final ApprovalService approvalService;

    @GetMapping
    public Flux<ApprovalResponse> list(@RequestParam(required = false) String workflowRunId,
                                       @RequestParam(required = false) ApprovalStatus status) {
        return approvalService.list(workflowRunId, status);
    }

    @GetMapping("/{id}")
    public Mono<ApprovalResponse> get(@PathVariable UUID id) {
        return approvalService.get(id);
    }

    @PostMapping
    public Mono<ApprovalResponse> create(@Valid @RequestBody ApprovalRequest request) {
        return approvalService.create(request);
    }

    @PostMapping("/{id}/approve")
    public Mono<ApprovalResponse> approve(@PathVariable UUID id,
                                          @RequestHeader(value = "X-User-Id", defaultValue = "demo-approver") String userId) {
        return approvalService.approve(id, userId);
    }

    @PostMapping("/{id}/reject")
    public Mono<ApprovalResponse> reject(@PathVariable UUID id,
                                         @RequestHeader(value = "X-User-Id", defaultValue = "demo-approver") String userId) {
        return approvalService.reject(id, userId);
    }
}
