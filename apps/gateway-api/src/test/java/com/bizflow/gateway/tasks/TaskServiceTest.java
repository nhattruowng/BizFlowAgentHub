package com.bizflow.gateway.tasks;

import com.bizflow.gateway.config.TenantIdentifierResolver;
import com.bizflow.gateway.workflow.WorkflowClient;
import com.bizflow.shared.contracts.TaskRequest;
import com.bizflow.shared.contracts.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskInputRepository taskInputRepository;

    @Mock
    private WorkflowClient workflowClient;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(
                taskRepository,
                taskInputRepository,
                workflowClient,
                new ObjectMapper(),
                new TenantIdentifierResolver()
        );
    }

    @Test
    void createResolvesTenantAliasAndQueuesWorkflow() {
        UUID taskId = UUID.randomUUID();
        TaskEntity savedTask = TaskEntity.builder()
                .id(taskId)
                .tenantId(TenantIdentifierResolver.DEFAULT_TENANT_ID)
                .source("email")
                .type("email_support")
                .status(TaskStatus.CREATED)
                .correlationId("corr-1")
                .createdAt(Instant.parse("2026-04-02T01:00:00Z"))
                .updatedAt(Instant.parse("2026-04-02T01:00:00Z"))
                .build();
        TaskEntity queuedTask = savedTask.toBuilder()
                .status(TaskStatus.QUEUED)
                .workflowRunId("run-123")
                .updatedAt(Instant.parse("2026-04-02T01:01:00Z"))
                .build();

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(Mono.just(savedTask), Mono.just(queuedTask));
        when(taskInputRepository.save(any(TaskInputEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(workflowClient.startWorkflow(any())).thenReturn(Mono.just(WorkflowClient.WorkflowRunResponse.builder()
                .runId("run-123")
                .status("QUEUED")
                .build()));

        TaskRequest request = TaskRequest.builder()
                .tenantId("demo-tenant")
                .source("email")
                .type("email_support")
                .payload(Map.of("subject", "Need help"))
                .correlationId("corr-1")
                .build();

        var response = taskService.create(request).block();

        assertThat(response).isNotNull();
        assertThat(response.getTaskId()).isEqualTo(taskId.toString());
        assertThat(response.getStatus()).isEqualTo(TaskStatus.QUEUED);
        assertThat(response.getWorkflowRunId()).isEqualTo("run-123");

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository, times(2)).save(taskCaptor.capture());
        assertThat(taskCaptor.getAllValues().getFirst().getTenantId()).isEqualTo(TenantIdentifierResolver.DEFAULT_TENANT_ID);
    }

    @Test
    void createMarksTaskFailedWhenWorkflowStartFails() {
        UUID taskId = UUID.randomUUID();
        TaskEntity savedTask = TaskEntity.builder()
                .id(taskId)
                .tenantId(TenantIdentifierResolver.DEFAULT_TENANT_ID)
                .source("upload")
                .type("invoice_approval")
                .status(TaskStatus.CREATED)
                .correlationId("corr-2")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        TaskEntity failedTask = savedTask.toBuilder()
                .status(TaskStatus.FAILED)
                .updatedAt(Instant.now())
                .build();

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(Mono.just(savedTask), Mono.just(failedTask));
        when(taskInputRepository.save(any(TaskInputEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(workflowClient.startWorkflow(any())).thenReturn(Mono.error(new IllegalStateException("workflow down")));

        TaskRequest request = TaskRequest.builder()
                .tenantId(TenantIdentifierResolver.DEFAULT_TENANT_ID)
                .source("upload")
                .type("invoice_approval")
                .payload(Map.of("invoiceId", "INV-1"))
                .build();

        assertThatThrownBy(() -> taskService.create(request).block())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to start workflow");
    }

    @Test
    void getDetailsIncludesStoredPayload() {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .tenantId(TenantIdentifierResolver.DEFAULT_TENANT_ID)
                .source("internal")
                .type("policy_lookup")
                .status(TaskStatus.COMPLETED)
                .workflowRunId("run-456")
                .correlationId("corr-3")
                .createdAt(Instant.parse("2026-04-02T02:00:00Z"))
                .updatedAt(Instant.parse("2026-04-02T02:05:00Z"))
                .build();
        TaskInputEntity inputEntity = TaskInputEntity.builder()
                .taskId(taskId)
                .payloadJson("{\"query\":\"invoice\"}")
                .createdAt(Instant.parse("2026-04-02T02:00:10Z"))
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Mono.just(task));
        when(taskInputRepository.findLatestByTaskId(taskId)).thenReturn(Mono.just(inputEntity));

        var response = taskService.getDetails(taskId).block();

        assertThat(response).isNotNull();
        assertThat(response.getTaskId()).isEqualTo(taskId.toString());
        assertThat(response.getWorkflowName()).isEqualTo("policy-lookup-workflow");
        assertThat(response.getPayload()).containsEntry("query", "invoice");
    }
}
