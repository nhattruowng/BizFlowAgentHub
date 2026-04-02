package com.bizflow.approval.core;

import com.bizflow.shared.contracts.ApprovalStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ApprovalRepository extends ReactiveCrudRepository<ApprovalEntity, UUID> {
    Flux<ApprovalEntity> findByStatus(ApprovalStatus status);
}
