package com.bizflow.toolhub.tools;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ToolCallRepository extends JpaRepository<ToolCallEntity, UUID> {
}
