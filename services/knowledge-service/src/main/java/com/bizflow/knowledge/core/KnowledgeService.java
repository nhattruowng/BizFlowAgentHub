package com.bizflow.knowledge.core;

import com.bizflow.knowledge.api.KnowledgeSearchRequest;
import com.bizflow.knowledge.api.KnowledgeSearchResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {
    private final KnowledgeDocRepository docRepository;
    private final KnowledgeChunkRepository chunkRepository;

    public KnowledgeService(KnowledgeDocRepository docRepository, KnowledgeChunkRepository chunkRepository) {
        this.docRepository = docRepository;
        this.chunkRepository = chunkRepository;
    }

    public List<KnowledgeSearchResult> search(KnowledgeSearchRequest request) {
        List<KnowledgeChunkEntity> chunks = chunkRepository.search(request.getQuery());
        Map<UUID, KnowledgeDocEntity> docs = docRepository.findAllById(
                chunks.stream().map(KnowledgeChunkEntity::getDocId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(KnowledgeDocEntity::getId, d -> d));

        return chunks.stream()
                .limit(request.getLimit())
                .map(chunk -> {
                    KnowledgeDocEntity doc = docs.get(chunk.getDocId());
                    return new KnowledgeSearchResult(
                            chunk.getDocId().toString(),
                            chunk.getId().toString(),
                            doc != null ? doc.getTitle() : "",
                            doc != null ? doc.getSource() : "",
                            chunk.getContent()
                    );
                })
                .toList();
    }
}
