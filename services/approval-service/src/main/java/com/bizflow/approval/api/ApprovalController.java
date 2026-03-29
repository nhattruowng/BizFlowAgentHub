package com.bizflow.approval.api;

import com.bizflow.approval.core.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalResponse>> listPending() {
        return ResponseEntity.ok(approvalService.listPending());
    }

    @PostMapping
    public ResponseEntity<ApprovalResponse> create(@Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(approvalService.create(request));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approve(@PathVariable UUID id,
                                                    @RequestHeader(value = "X-User-Id", defaultValue = "demo-approver") String userId) {
        return ResponseEntity.ok(approvalService.approve(id, userId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalResponse> reject(@PathVariable UUID id,
                                                   @RequestHeader(value = "X-User-Id", defaultValue = "demo-approver") String userId) {
        return ResponseEntity.ok(approvalService.reject(id, userId));
    }
}
