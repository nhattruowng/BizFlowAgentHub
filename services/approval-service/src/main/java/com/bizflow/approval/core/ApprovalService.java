package com.bizflow.approval.core;

import com.bizflow.approval.api.ApprovalRequest;
import com.bizflow.approval.api.ApprovalResponse;
import com.bizflow.shared.contracts.ApprovalStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {
    private final ApprovalRepository repository;

    public ApprovalService(ApprovalRepository repository) {
        this.repository = repository;
    }

    public ApprovalResponse create(ApprovalRequest request) {
        ApprovalEntity entity = new ApprovalEntity();
        entity.setWorkflowRunId(request.getWorkflowRunId());
        entity.setRequestedBy(request.getRequestedBy());
        entity.setReason(request.getReason());
        entity.setStatus(ApprovalStatus.PENDING);
        entity = repository.save(entity);
        return map(entity);
    }

    public List<ApprovalResponse> listPending() {
        return repository.findByStatus(ApprovalStatus.PENDING).stream().map(this::map).toList();
    }

    public ApprovalResponse approve(UUID id, String decidedBy) {
        ApprovalEntity entity = repository.findById(id).orElseThrow();
        entity.setStatus(ApprovalStatus.APPROVED);
        entity.setDecidedBy(decidedBy);
        entity = repository.save(entity);
        return map(entity);
    }

    public ApprovalResponse reject(UUID id, String decidedBy) {
        ApprovalEntity entity = repository.findById(id).orElseThrow();
        entity.setStatus(ApprovalStatus.REJECTED);
        entity.setDecidedBy(decidedBy);
        entity = repository.save(entity);
        return map(entity);
    }

    private ApprovalResponse map(ApprovalEntity entity) {
        return new ApprovalResponse(
                entity.getId().toString(),
                entity.getWorkflowRunId(),
                entity.getStatus(),
                entity.getRequestedBy(),
                entity.getDecidedBy(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }
}
