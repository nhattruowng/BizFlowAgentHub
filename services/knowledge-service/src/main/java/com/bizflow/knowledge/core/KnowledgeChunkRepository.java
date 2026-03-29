package com.bizflow.knowledge.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunkEntity, UUID> {
    @Query("select c from KnowledgeChunkEntity c where lower(c.content) like lower(concat('%', :query, '%'))")
    List<KnowledgeChunkEntity> search(@Param("query") String query);
}
