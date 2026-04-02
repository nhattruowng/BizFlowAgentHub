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
@Table("knowledge_docs")
public class KnowledgeDocEntity {
    @Id
    private UUID id;

    @Column("title")
    private String title;

    @Column("source")
    private String source;

    @Column("created_at")
    private Instant createdAt;
}
