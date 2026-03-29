package com.bizflow.workflow.engine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowRunRepository extends JpaRepository<WorkflowRunEntity, UUID> {
}
