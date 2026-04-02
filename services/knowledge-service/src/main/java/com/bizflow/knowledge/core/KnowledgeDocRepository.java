package com.bizflow.knowledge.core;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface KnowledgeDocRepository extends ReactiveCrudRepository<KnowledgeDocEntity, UUID> {
}
