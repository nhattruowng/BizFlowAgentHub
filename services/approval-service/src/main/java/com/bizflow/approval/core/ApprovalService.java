package com.bizflow.approval.core;

import com.bizflow.approval.api.ApprovalRequest;
import com.bizflow.approval.api.ApprovalResponse;
import com.bizflow.shared.contracts.ApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository repository;

    public Mono<ApprovalResponse> create(ApprovalRequest request) {
        Instant now = Instant.now();
        return repository.save(ApprovalEntity.builder()
                        .workflowRunId(request.getWorkflowRunId())
                        .requestedBy(request.getRequestedBy())
                        .reason(request.getReason())
                        .status(ApprovalStatus.PENDING)
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .map(this::map);
    }

    public Flux<ApprovalResponse> listPending() {
        return repository.findByStatus(ApprovalStatus.PENDING).map(this::map);
    }

    public Mono<ApprovalResponse> approve(UUID id, String decidedBy) {
        return updateDecision(id, decidedBy, ApprovalStatus.APPROVED);
    }

    public Mono<ApprovalResponse> reject(UUID id, String decidedBy) {
        return updateDecision(id, decidedBy, ApprovalStatus.REJECTED);
    }

    private Mono<ApprovalResponse> updateDecision(UUID id, String decidedBy, ApprovalStatus status) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Approval not found")))
                .flatMap(entity -> repository.save(entity.toBuilder()
                                .status(status)
                                .decidedBy(decidedBy)
                                .updatedAt(Instant.now())
                                .build()))
                .map(this::map);
    }

    private ApprovalResponse map(ApprovalEntity entity) {
        return ApprovalResponse.builder()
                .id(entity.getId().toString())
                .workflowRunId(entity.getWorkflowRunId())
                .status(entity.getStatus())
                .requestedBy(entity.getRequestedBy())
                .decidedBy(entity.getDecidedBy())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
