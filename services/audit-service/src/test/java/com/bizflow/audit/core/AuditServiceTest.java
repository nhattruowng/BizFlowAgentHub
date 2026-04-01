package com.bizflow.audit.core;

import com.bizflow.audit.api.AuditLogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
    @Mock
    private AuditLogRepository repository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(repository);
    }

    @Test
    void appendPersistsAuditLog() {
        AuditLogEntity saved = AuditLogEntity.builder()
                .id(UUID.randomUUID())
                .workflowRunId("run-1")
                .action("TOOL_INVOKED")
                .payload("{\"tool\":\"read_policy_docs\"}")
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();
        when(repository.save(any(AuditLogEntity.class))).thenReturn(Mono.just(saved));

        var response = auditService.append(AuditLogRequest.builder()
                .workflowRunId("run-1")
                .action("TOOL_INVOKED")
                .payload("{\"tool\":\"read_policy_docs\"}")
                .build()).block();

        assertThat(response).isNotNull();
        assertThat(response.getAction()).isEqualTo("TOOL_INVOKED");
    }

    @Test
    void listFiltersByAction() {
        AuditLogEntity log = AuditLogEntity.builder()
                .id(UUID.randomUUID())
                .workflowRunId("run-2")
                .action("APPROVAL_APPROVED")
                .payload("{\"approvalId\":\"a-1\"}")
                .createdAt(Instant.parse("2026-04-02T03:10:00Z"))
                .build();
        when(repository.findByAction("APPROVAL_APPROVED")).thenReturn(Flux.just(log));

        List<AuditLogEntity> logs = auditService.list(null, "APPROVAL_APPROVED").collectList().block();

        assertThat(logs).hasSize(1);
        assertThat(logs.getFirst().getWorkflowRunId()).isEqualTo("run-2");
    }
}
