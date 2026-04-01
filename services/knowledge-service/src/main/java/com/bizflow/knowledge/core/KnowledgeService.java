package com.bizflow.knowledge.core;

import com.bizflow.knowledge.api.KnowledgeDocumentResponse;
import com.bizflow.knowledge.api.KnowledgeSearchRequest;
import com.bizflow.knowledge.api.KnowledgeSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class KnowledgeService {
    private final KnowledgeDocRepository docRepository;
    private final KnowledgeChunkRepository chunkRepository;

    public Flux<KnowledgeDocumentResponse> listDocuments() {
        return docRepository.findAll()
                .sort(Comparator.comparing(KnowledgeDocEntity::getTitle))
                .map(this::mapDocument);
    }

    public Flux<KnowledgeSearchResult> search(KnowledgeSearchRequest request) {
        int limit = Math.max(1, Math.min(request.getLimit(), 20));
        String normalizedQuery = request.getQuery().trim().toLowerCase(Locale.ROOT);

        return chunkRepository.search(request.getQuery())
                .collectList()
                .flatMapMany(chunks -> docRepository.findAllById(chunks.stream().map(KnowledgeChunkEntity::getDocId).distinct().toList())
                        .collectMap(KnowledgeDocEntity::getId, Function.identity())
                        .flatMapMany(docs -> Flux.fromIterable(chunks)
                                .map(chunk -> mapResult(chunk, docs, normalizedQuery))
                                .sort(Comparator.comparing(KnowledgeSearchResult::getScore).reversed())
                                .take(limit)));
    }

    private KnowledgeSearchResult mapResult(KnowledgeChunkEntity chunk,
                                            Map<UUID, KnowledgeDocEntity> docs,
                                            String normalizedQuery) {
        KnowledgeDocEntity doc = docs.get(chunk.getDocId());
        return KnowledgeSearchResult.builder()
                .docId(chunk.getDocId().toString())
                .chunkId(chunk.getId().toString())
                .title(doc != null ? doc.getTitle() : "")
                .source(doc != null ? doc.getSource() : "")
                .content(chunk.getContent())
                .score(score(normalizedQuery, chunk, doc))
                .build();
    }

    private int score(String normalizedQuery, KnowledgeChunkEntity chunk, KnowledgeDocEntity doc) {
        int score = 50;
        if (chunk.getContent() != null && chunk.getContent().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
            score += 20;
        }
        if (doc != null && doc.getTitle() != null && doc.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
            score += 25;
        }
        if (doc != null && doc.getSource() != null && doc.getSource().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
            score += 10;
        }
        score -= chunk.getChunkIndex();
        return score;
    }

    private KnowledgeDocumentResponse mapDocument(KnowledgeDocEntity doc) {
        return KnowledgeDocumentResponse.builder()
                .docId(doc.getId().toString())
                .title(doc.getTitle())
                .source(doc.getSource())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
