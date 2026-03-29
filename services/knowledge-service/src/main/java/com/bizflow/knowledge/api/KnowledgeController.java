package com.bizflow.knowledge.api;

import com.bizflow.knowledge.core.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<KnowledgeSearchResult>> search(@Valid @RequestBody KnowledgeSearchRequest request) {
        return ResponseEntity.ok(knowledgeService.search(request));
    }
}
