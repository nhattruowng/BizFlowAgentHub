package com.bizflow.knowledge.core;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface KnowledgeChunkRepository extends ReactiveCrudRepository<KnowledgeChunkEntity, UUID> {
    @Query("select id, doc_id, chunk_index, content, created_at from knowledge_chunks " +
            "where lower(content) like lower(concat('%', :query, '%')) order by chunk_index")
    Flux<KnowledgeChunkEntity> search(@Param("query") String query);
}
