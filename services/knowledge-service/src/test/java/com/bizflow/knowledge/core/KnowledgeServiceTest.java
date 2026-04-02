package com.bizflow.knowledge.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {
    @Mock
    private KnowledgeDocRepository docRepository;

    @Mock
    private KnowledgeChunkRepository chunkRepository;

    private KnowledgeService knowledgeService;

    @BeforeEach
    void setUp() {
        knowledgeService = new KnowledgeService(docRepository, chunkRepository);
    }

    @Test
    void listDocumentsReturnsAlphabeticalOrder() {
        KnowledgeDocEntity invoice = KnowledgeDocEntity.builder()
                .id(UUID.randomUUID())
                .title("Invoice Policy")
                .source("internal/policies/invoice.md")
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();
        KnowledgeDocEntity support = KnowledgeDocEntity.builder()
                .id(UUID.randomUUID())
                .title("Support SLA")
                .source("internal/policies/support-sla.md")
                .createdAt(Instant.parse("2026-04-02T03:01:00Z"))
                .build();

        when(docRepository.findAll()).thenReturn(Flux.just(support, invoice));

        var results = knowledgeService.listDocuments().collectList().block();

        assertThat(results).extracting("title").containsExactly("Invoice Policy", "Support SLA");
    }

    @Test
    void searchBoostsTitleMatches() {
        UUID invoiceDocId = UUID.randomUUID();
        UUID supportDocId = UUID.randomUUID();
        KnowledgeChunkEntity invoiceChunk = KnowledgeChunkEntity.builder()
                .id(UUID.randomUUID())
                .docId(invoiceDocId)
                .chunkIndex(0)
                .content("Invoices above 1000 require supervisor approval.")
                .createdAt(Instant.parse("2026-04-02T03:10:00Z"))
                .build();
        KnowledgeChunkEntity supportChunk = KnowledgeChunkEntity.builder()
                .id(UUID.randomUUID())
                .docId(supportDocId)
                .chunkIndex(0)
                .content("Support teams review invoices for exception handling.")
                .createdAt(Instant.parse("2026-04-02T03:11:00Z"))
                .build();
        KnowledgeDocEntity invoiceDoc = KnowledgeDocEntity.builder()
                .id(invoiceDocId)
                .title("Invoice Policy")
                .source("internal/policies/invoice.md")
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();
        KnowledgeDocEntity supportDoc = KnowledgeDocEntity.builder()
                .id(supportDocId)
                .title("Support SLA")
                .source("internal/policies/support-sla.md")
                .createdAt(Instant.parse("2026-04-02T03:01:00Z"))
                .build();

        when(chunkRepository.search("invoice")).thenReturn(Flux.just(supportChunk, invoiceChunk));
        when(docRepository.findAllById(anyIterable())).thenReturn(Flux.just(invoiceDoc, supportDoc));

        var results = knowledgeService.search(com.bizflow.knowledge.api.KnowledgeSearchRequest.builder()
                        .query("invoice")
                        .limit(5)
                        .build())
                .collectList()
                .block();

        assertThat(results).isNotNull();
        assertThat(results.getFirst().getTitle()).isEqualTo("Invoice Policy");
        assertThat(results.getFirst().getScore()).isGreaterThan(results.get(1).getScore());
    }
}
