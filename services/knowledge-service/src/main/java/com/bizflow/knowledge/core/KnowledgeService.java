package com.bizflow.knowledge.core;

import com.bizflow.knowledge.api.KnowledgeSearchRequest;
import com.bizflow.knowledge.api.KnowledgeSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class KnowledgeService {
    private final KnowledgeDocRepository docRepository;
    private final KnowledgeChunkRepository chunkRepository;

    public Flux<KnowledgeSearchResult> search(KnowledgeSearchRequest request) {
        return chunkRepository.search(request.getQuery())
                .take(Math.max(1, request.getLimit()))
                .collectList()
                .flatMapMany(chunks -> docRepository.findAllById(chunks.stream().map(KnowledgeChunkEntity::getDocId).distinct().toList())
                        .collectMap(KnowledgeDocEntity::getId, Function.identity())
                        .flatMapMany(docs -> Flux.fromIterable(chunks)
                                .map(chunk -> mapResult(chunk, docs))));
    }

    private KnowledgeSearchResult mapResult(KnowledgeChunkEntity chunk, Map<UUID, KnowledgeDocEntity> docs) {
        KnowledgeDocEntity doc = docs.get(chunk.getDocId());
        return KnowledgeSearchResult.builder()
                .docId(chunk.getDocId().toString())
                .chunkId(chunk.getId().toString())
                .title(doc != null ? doc.getTitle() : "")
                .source(doc != null ? doc.getSource() : "")
                .content(chunk.getContent())
                .build();
    }
}
