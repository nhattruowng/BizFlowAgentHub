package com.bizflow.approval.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<ApprovalEntity, UUID> {
    List<ApprovalEntity> findByStatus(com.bizflow.shared.contracts.ApprovalStatus status);
}
