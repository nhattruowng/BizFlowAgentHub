package com.bizflow.toolhub.tools;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ToolCallRepository extends ReactiveCrudRepository<ToolCallEntity, UUID> {
}
