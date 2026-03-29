package com.bizflow.toolhub.tools;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ToolRepository extends JpaRepository<ToolEntity, UUID> {
    Optional<ToolEntity> findByToolName(String toolName);
}
