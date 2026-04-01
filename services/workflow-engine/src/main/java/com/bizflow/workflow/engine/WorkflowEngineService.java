package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.workflow.api.WorkflowRunRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository runRepository;
    private final WorkflowStepRepository stepRepository;

    public Mono<WorkflowRunResponse> start(WorkflowRunRequest request) {
        return workflowRepository.findByName(request.getWorkflowName())
                .switchIfEmpty(createWorkflow(request.getWorkflowName()))
                .flatMap(workflow -> createWorkflowRun(workflow, request)
                        .flatMap(run -> persistSteps(run, workflow.getName())
                                .collectList()
                                .flatMap(steps -> finalizeRun(run, workflow.getName())
                                        .map(updatedRun -> mapResponse(updatedRun, workflow.getName(), steps)))));
    }

    public Mono<WorkflowRunResponse> get(UUID runId) {
        return runRepository.findById(runId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow run not found")))
                .flatMap(run -> Mono.zip(
                        workflowRepository.findById(run.getWorkflowId())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found"))),
                        stepRepository.findByRunId(run.getId()).collectList(),
                        (workflow, steps) -> mapResponse(run, workflow.getName(), steps)
                ));
    }

    private Mono<WorkflowEntity> createWorkflow(String name) {
        return workflowRepository.save(WorkflowEntity.builder()
                .name(name)
                .description("Auto-registered workflow for " + name)
                .createdAt(Instant.now())
                .build());
    }

    private Mono<WorkflowRunEntity> createWorkflowRun(WorkflowEntity workflow, WorkflowRunRequest request) {
        Instant now = Instant.now();
        return runRepository.save(WorkflowRunEntity.builder()
                .workflowId(workflow.getId())
                .taskId(request.getTaskId())
                .correlationId(request.getCorrelationId())
                .status(WorkflowStatus.QUEUED)
                .startedAt(now)
                .updatedAt(now)
                .build());
    }

    private Flux<WorkflowStepEntity> persistSteps(WorkflowRunEntity run, String workflowName) {
        Instant now = Instant.now();
        return stepRepository.saveAll(stepNamesFor(workflowName).stream()
                .map(stepName -> WorkflowStepEntity.builder()
                        .runId(run.getId())
                        .stepName(stepName)
                        .attempt(1)
                        .startedAt(now)
                        .endedAt(now)
                        .status(statusForStep(stepName, workflowName))
                        .build())
                .toList());
    }

    private Mono<WorkflowRunEntity> finalizeRun(WorkflowRunEntity run, String workflowName) {
        return runRepository.save(run.toBuilder()
                .status(determineFinalStatus(workflowName))
                .updatedAt(Instant.now())
                .build());
    }

    private WorkflowRunResponse mapResponse(WorkflowRunEntity run, String workflowName, List<WorkflowStepEntity> steps) {
        return WorkflowRunResponse.builder()
                .runId(run.getId().toString())
                .workflowName(workflowName)
                .status(run.getStatus())
                .correlationId(run.getCorrelationId())
                .startedAt(run.getStartedAt())
                .updatedAt(run.getUpdatedAt())
                .steps(steps.stream().map(WorkflowStepEntity::getStepName).toList())
                .build();
    }

    private List<String> stepNamesFor(String workflowName) {
        return switch (workflowName) {
            case "invoice-approval-workflow" -> List.of(
                    "ROUTER_AGENT",
                    "CONTEXT_AGENT",
                    "POLICY_AGENT",
                    "WAITING_APPROVAL"
            );
            case "policy-lookup-workflow" -> List.of(
                    "ROUTER_AGENT",
                    "KNOWLEDGE_AGENT",
                    "VALIDATOR_AGENT"
            );
            default -> List.of(
                    "ROUTER_AGENT",
                    "PLANNER_AGENT",
                    "EXECUTOR_AGENT",
                    "VALIDATOR_AGENT"
            );
        };
    }

    private WorkflowStatus statusForStep(String stepName, String workflowName) {
        if ("invoice-approval-workflow".equals(workflowName) && "WAITING_APPROVAL".equals(stepName)) {
            return WorkflowStatus.WAITING_APPROVAL;
        }
        return WorkflowStatus.COMPLETED;
    }

    private WorkflowStatus determineFinalStatus(String workflowName) {
        if ("invoice-approval-workflow".equals(workflowName)) {
            return WorkflowStatus.WAITING_APPROVAL;
        }
        return WorkflowStatus.COMPLETED;
    }
}
