package com.bizflow.knowledge.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSearchResult {
    private String docId;
    private String chunkId;
    private String title;
    private String source;
    private String content;
}
