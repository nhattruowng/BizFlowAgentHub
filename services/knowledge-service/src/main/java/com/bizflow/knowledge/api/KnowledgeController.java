package com.bizflow.knowledge.api;

import com.bizflow.knowledge.core.KnowledgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    @GetMapping("/documents")
    public Flux<KnowledgeDocumentResponse> listDocuments() {
        return knowledgeService.listDocuments();
    }

    @PostMapping("/search")
    public Flux<KnowledgeSearchResult> search(@Valid @RequestBody KnowledgeSearchRequest request) {
        return knowledgeService.search(request);
    }
}
