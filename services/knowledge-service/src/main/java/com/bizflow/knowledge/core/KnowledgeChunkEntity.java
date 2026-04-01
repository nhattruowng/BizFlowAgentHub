package com.bizflow.knowledge.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("knowledge_chunks")
public class KnowledgeChunkEntity {
    @Id
    private UUID id;

    @Column("doc_id")
    private UUID docId;

    @Column("chunk_index")
    private int chunkIndex;

    @Column("content")
    private String content;

    @Column("created_at")
    private Instant createdAt;
}
