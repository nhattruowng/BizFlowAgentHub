package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.workflow.api.WorkflowRunRequest;
import com.bizflow.workflow.events.WorkflowOutboxService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceTest {
    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private WorkflowRunRepository runRepository;

    @Mock
    private WorkflowStepRepository stepRepository;

    @Mock
    private WorkflowOutboxService workflowOutboxService;

    private WorkflowEngineService workflowEngineService;

    @BeforeEach
    void setUp() {
        workflowEngineService = new WorkflowEngineService(
                workflowRepository,
                runRepository,
                stepRepository,
                new WorkflowCatalog(),
                workflowOutboxService
        );
    }

    @Test
    void startInvoiceWorkflowCreatesWaitingApprovalRun() {
        UUID workflowId = UUID.randomUUID();
        UUID runId = UUID.randomUUID();
        WorkflowEntity workflow = WorkflowEntity.builder()
                .id(workflowId)
                .name("invoice-approval-workflow")
                .description("Invoice OCR, policy review and approval gate")
                .createdAt(Instant.parse("2026-04-02T03:00:00Z"))
                .build();
        WorkflowRunEntity savedRun = WorkflowRunEntity.builder()
                .id(runId)
                .workflowId(workflowId)
                .taskId("task-1")
                .correlationId("corr-1")
                .status(WorkflowStatus.COMPLETED)
                .startedAt(Instant.parse("2026-04-02T03:01:00Z"))
                .updatedAt(Instant.parse("2026-04-02T03:01:00Z"))
                .build();
        WorkflowRunEntity updatedRun = savedRun.toBuilder()
                .status(WorkflowStatus.WAITING_APPROVAL)
                .updatedAt(Instant.parse("2026-04-02T03:02:00Z"))
                .build();

        when(workflowRepository.findByName("invoice-approval-workflow")).thenReturn(Mono.just(workflow));
        when(runRepository.save(any(WorkflowRunEntity.class))).thenReturn(Mono.just(savedRun), Mono.just(updatedRun));
        when(stepRepository.saveAll(any(Iterable.class))).thenAnswer(invocation -> Flux.fromIterable(invocation.getArgument(0)));
        when(workflowOutboxService.appendWorkflowRunCreatedEvent(any(), any())).thenReturn(Mono.empty());

        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .workflowName("invoice-approval-workflow")
                .tenantId("11111111-1111-1111-1111-111111111111")
                .taskId("task-1")
                .correlationId("corr-1")
                .build();

        var response = workflowEngineService.start(request).block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(WorkflowStatus.WAITING_APPROVAL);
        assertThat(response.getCurrentStep()).isEqualTo("WAITING_APPROVAL");
        assertThat(response.getSteps()).containsExactly(
                "ROUTER_AGENT",
                "CONTEXT_AGENT",
                "POLICY_AGENT",
                "WAITING_APPROVAL"
        );
        assertThat(response.getStepDetails()).hasSize(4);
        verify(workflowOutboxService).appendWorkflowRunCreatedEvent(any(), any());
    }

    @Test
    void startRejectsUnknownWorkflow() {
        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .workflowName("unknown-workflow")
                .tenantId("11111111-1111-1111-1111-111111111111")
                .taskId("task-2")
                .build();

        assertThatThrownBy(() -> workflowEngineService.start(request).block())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Workflow is not registered");
    }

    @Test
    void getReturnsStoredStepDetails() {
        UUID workflowId = UUID.randomUUID();
        UUID runId = UUID.randomUUID();
        WorkflowRunEntity run = WorkflowRunEntity.builder()
                .id(runId)
                .workflowId(workflowId)
                .taskId("task-3")
                .correlationId("corr-3")
                .status(WorkflowStatus.COMPLETED)
                .startedAt(Instant.parse("2026-04-02T04:00:00Z"))
                .updatedAt(Instant.parse("2026-04-02T04:02:00Z"))
                .build();
        WorkflowEntity workflow = WorkflowEntity.builder()
                .id(workflowId)
                .name("policy-lookup-workflow")
                .description("Knowledge retrieval and validation flow")
                .createdAt(Instant.parse("2026-04-02T03:59:00Z"))
                .build();
        List<WorkflowStepEntity> steps = List.of(
                WorkflowStepEntity.builder()
                        .runId(runId)
                        .stepName("ROUTER_AGENT")
                        .status(WorkflowStatus.COMPLETED)
                        .attempt(1)
                        .startedAt(Instant.parse("2026-04-02T04:00:00Z"))
                        .endedAt(Instant.parse("2026-04-02T04:00:01Z"))
                        .build(),
                WorkflowStepEntity.builder()
                        .runId(runId)
                        .stepName("KNOWLEDGE_AGENT")
                        .status(WorkflowStatus.COMPLETED)
                        .attempt(1)
                        .startedAt(Instant.parse("2026-04-02T04:00:02Z"))
                        .endedAt(Instant.parse("2026-04-02T04:00:03Z"))
                        .build(),
                WorkflowStepEntity.builder()
                        .runId(runId)
                        .stepName("VALIDATOR_AGENT")
                        .status(WorkflowStatus.COMPLETED)
                        .attempt(1)
                        .startedAt(Instant.parse("2026-04-02T04:00:04Z"))
                        .endedAt(Instant.parse("2026-04-02T04:00:05Z"))
                        .build()
        );

        when(runRepository.findById(runId)).thenReturn(Mono.just(run));
        when(workflowRepository.findById(workflowId)).thenReturn(Mono.just(workflow));
        when(stepRepository.findByRunId(runId)).thenReturn(Flux.fromIterable(steps));

        var response = workflowEngineService.get(runId).block();

        assertThat(response).isNotNull();
        assertThat(response.getWorkflowName()).isEqualTo("policy-lookup-workflow");
        assertThat(response.getCurrentStep()).isEqualTo("VALIDATOR_AGENT");
        assertThat(response.getStepDetails()).hasSize(3);
    }
}
