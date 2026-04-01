package com.bizflow.approval.core;

import com.bizflow.approval.api.ApprovalRequest;
import com.bizflow.approval.api.ApprovalResponse;
import com.bizflow.shared.contracts.ApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
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

    public Flux<ApprovalResponse> list(String workflowRunId, ApprovalStatus status) {
        Flux<ApprovalEntity> approvals;
        if (StringUtils.hasText(workflowRunId) && status != null) {
            approvals = repository.findByWorkflowRunIdAndStatus(workflowRunId, status);
        } else if (StringUtils.hasText(workflowRunId)) {
            approvals = repository.findByWorkflowRunId(workflowRunId);
        } else if (status != null) {
            approvals = repository.findByStatus(status);
        } else {
            approvals = repository.findAll();
        }
        return approvals
                .sort(Comparator.comparing(ApprovalEntity::getCreatedAt).reversed())
                .map(this::map);
    }

    public Mono<ApprovalResponse> get(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Approval not found")))
                .map(this::map);
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
                .flatMap(entity -> {
                    if (entity.getStatus() != ApprovalStatus.PENDING) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Approval already decided with status " + entity.getStatus()
                        ));
                    }
                    return repository.save(entity.toBuilder()
                                    .status(status)
                                    .decidedBy(decidedBy)
                                    .updatedAt(Instant.now())
                                    .build())
                            .map(this::map);
                });
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
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
