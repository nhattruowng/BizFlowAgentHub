package com.bizflow.knowledge.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KnowledgeDocRepository extends JpaRepository<KnowledgeDocEntity, UUID> {
}
