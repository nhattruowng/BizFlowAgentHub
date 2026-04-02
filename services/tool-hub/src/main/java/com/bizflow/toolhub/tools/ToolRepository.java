package com.bizflow.toolhub.tools;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ToolRepository extends ReactiveCrudRepository<ToolEntity, UUID> {
    Mono<ToolEntity> findByToolName(String toolName);
}
