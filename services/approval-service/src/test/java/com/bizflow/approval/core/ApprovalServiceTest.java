package com.bizflow.approval.core;

import com.bizflow.approval.api.ApprovalRequest;
import com.bizflow.shared.contracts.ApprovalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {
    @Mock
    private ApprovalRepository repository;

    private ApprovalService approvalService;

    @BeforeEach
    void setUp() {
        approvalService = new ApprovalService(repository);
    }

    @Test
    void createInitializesPendingApproval() {
        UUID approvalId = UUID.randomUUID();
        ApprovalEntity entity = ApprovalEntity.builder()
                .id(approvalId)
                .workflowRunId("run-1")
                .requestedBy("demo-user")
                .reason("Invoice > 1000")
                .status(ApprovalStatus.PENDING)
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .updatedAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();
        when(repository.save(any(ApprovalEntity.class))).thenReturn(Mono.just(entity));

        ApprovalRequest request = ApprovalRequest.builder()
                .workflowRunId("run-1")
                .requestedBy("demo-user")
                .reason("Invoice > 1000")
                .build();

        var response = approvalService.create(request).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ApprovalStatus.PENDING);
        assertThat(response.getWorkflowRunId()).isEqualTo("run-1");
    }

    @Test
    void approveRejectsAlreadyDecidedApproval() {
        UUID approvalId = UUID.randomUUID();
        ApprovalEntity entity = ApprovalEntity.builder()
                .id(approvalId)
                .workflowRunId("run-2")
                .requestedBy("demo-user")
                .reason("Policy override")
                .status(ApprovalStatus.APPROVED)
                .decidedBy("lead-user")
                .createdAt(Instant.parse("2026-04-02T03:10:00Z"))
                .updatedAt(Instant.parse("2026-04-02T03:12:00Z"))
                .build();
        when(repository.findById(approvalId)).thenReturn(Mono.just(entity));

        assertThatThrownBy(() -> approvalService.approve(approvalId, "another-user").block())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already decided");
    }

    @Test
    void listFiltersByWorkflowRunAndStatus() {
        ApprovalEntity pending = ApprovalEntity.builder()
                .id(UUID.randomUUID())
                .workflowRunId("run-3")
                .requestedBy("demo-user")
                .reason("Needs review")
                .status(ApprovalStatus.PENDING)
                .createdAt(Instant.parse("2026-04-02T03:20:00Z"))
                .updatedAt(Instant.parse("2026-04-02T03:20:00Z"))
                .build();

        when(repository.findByWorkflowRunIdAndStatus("run-3", ApprovalStatus.PENDING)).thenReturn(Flux.just(pending));

        List<?> responses = approvalService.list("run-3", ApprovalStatus.PENDING).collectList().block();

        assertThat(responses).hasSize(1);
    }
}
