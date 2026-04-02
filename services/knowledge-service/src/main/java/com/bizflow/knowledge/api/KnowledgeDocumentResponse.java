package com.bizflow.knowledge.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocumentResponse {
    private String docId;
    private String title;
    private String source;
    private Instant createdAt;
}
