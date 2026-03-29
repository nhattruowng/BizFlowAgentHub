package com.bizflow.workflow.engine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<WorkflowEntity, UUID> {
    Optional<WorkflowEntity> findByName(String name);
}
