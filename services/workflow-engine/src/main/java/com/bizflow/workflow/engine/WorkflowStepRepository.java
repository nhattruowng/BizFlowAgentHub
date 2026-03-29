package com.bizflow.workflow.engine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStepEntity, UUID> {
    List<WorkflowStepEntity> findByRunId(UUID runId);
}
