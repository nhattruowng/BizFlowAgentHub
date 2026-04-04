package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.shared.contracts.WorkflowStepView;
import com.bizflow.workflow.api.WorkflowDefinitionResponse;
import com.bizflow.workflow.api.WorkflowRunRequest;
import com.bizflow.workflow.events.WorkflowOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository runRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowCatalog workflowCatalog;
    private final WorkflowOutboxService workflowOutboxService;

    public Flux<WorkflowDefinitionResponse> listDefinitions() {
        return workflowCatalog.listDefinitions();
    }

    @Transactional
    public Mono<WorkflowRunResponse> start(WorkflowRunRequest request) {
        WorkflowCatalog.WorkflowBlueprint blueprint = workflowCatalog.getRequired(request.getWorkflowName());
        return workflowRepository.findByName(blueprint.getName())
                .switchIfEmpty(Mono.defer(() -> createWorkflow(blueprint)))
                .flatMap(workflow -> createWorkflowRun(workflow, request, blueprint)
                        .flatMap(run -> persistSteps(run, blueprint)
                                .collectList()
                                .flatMap(steps -> finalizeRun(run, blueprint)
                                        .flatMap(updatedRun -> {
                                            WorkflowRunResponse response = mapResponse(updatedRun, blueprint.getName(), steps);
                                            return workflowOutboxService.appendWorkflowRunCreatedEvent(response, request)
                                                    .thenReturn(response);
                                        }))));
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

    private Mono<WorkflowEntity> createWorkflow(WorkflowCatalog.WorkflowBlueprint blueprint) {
        return workflowRepository.save(WorkflowEntity.builder()
                .name(blueprint.getName())
                .description(blueprint.getDescription())
                .createdAt(Instant.now())
                .build());
    }

    private Mono<WorkflowRunEntity> createWorkflowRun(WorkflowEntity workflow,
                                                      WorkflowRunRequest request,
                                                      WorkflowCatalog.WorkflowBlueprint blueprint) {
        Instant now = Instant.now();
        return runRepository.save(WorkflowRunEntity.builder()
                .workflowId(workflow.getId())
                .taskId(request.getTaskId())
                .correlationId(request.getCorrelationId())
                .status(initialRunStatus(blueprint))
                .startedAt(now)
                .updatedAt(now)
                .build());
    }

    private Flux<WorkflowStepEntity> persistSteps(WorkflowRunEntity run, WorkflowCatalog.WorkflowBlueprint blueprint) {
        Instant now = Instant.now();
        return stepRepository.saveAll(IntStream.range(0, blueprint.getSteps().size())
                .mapToObj(index -> {
                    WorkflowCatalog.WorkflowStepPlan stepPlan = blueprint.getSteps().get(index);
                    Instant startedAt = now.plusMillis(index * 100L);
                    Instant endedAt = stepPlan.status() == WorkflowStatus.WAITING_APPROVAL ? null : startedAt.plusMillis(50);
                    return WorkflowStepEntity.builder()
                            .runId(run.getId())
                            .stepName(stepPlan.stepName())
                            .attempt(1)
                            .startedAt(startedAt)
                            .endedAt(endedAt)
                            .status(stepPlan.status())
                            .build();
                })
                .toList());
    }

    private Mono<WorkflowRunEntity> finalizeRun(WorkflowRunEntity run, WorkflowCatalog.WorkflowBlueprint blueprint) {
        return runRepository.save(run.toBuilder()
                .status(blueprint.getTerminalStatus())
                .updatedAt(Instant.now())
                .build());
    }

    private WorkflowRunResponse mapResponse(WorkflowRunEntity run, String workflowName, List<WorkflowStepEntity> steps) {
        List<WorkflowStepEntity> sortedSteps = steps.stream()
                .sorted(Comparator.comparing(WorkflowStepEntity::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        return WorkflowRunResponse.builder()
                .runId(run.getId().toString())
                .workflowName(workflowName)
                .status(run.getStatus())
                .currentStep(resolveCurrentStep(sortedSteps))
                .correlationId(run.getCorrelationId())
                .errorReason(run.getErrorReason())
                .startedAt(run.getStartedAt())
                .updatedAt(run.getUpdatedAt())
                .steps(sortedSteps.stream().map(WorkflowStepEntity::getStepName).toList())
                .stepDetails(sortedSteps.stream().map(this::mapStepView).toList())
                .build();
    }

    private WorkflowStatus initialRunStatus(WorkflowCatalog.WorkflowBlueprint blueprint) {
        if (blueprint.getSteps().isEmpty()) {
            return WorkflowStatus.QUEUED;
        }
        return blueprint.getSteps().get(0).status();
    }

    private String resolveCurrentStep(List<WorkflowStepEntity> steps) {
        for (WorkflowStepEntity step : steps) {
            if (!isTerminalStepStatus(step.getStatus())) {
                return step.getStepName();
            }
        }
        if (steps.isEmpty()) {
            return null;
        }
        return steps.get(steps.size() - 1).getStepName();
    }

    private boolean isTerminalStepStatus(WorkflowStatus status) {
        return status == WorkflowStatus.COMPLETED
                || status == WorkflowStatus.APPROVED
                || status == WorkflowStatus.REJECTED;
    }

    private WorkflowStepView mapStepView(WorkflowStepEntity entity) {
        return WorkflowStepView.builder()
                .stepName(entity.getStepName())
                .status(entity.getStatus())
                .attempt(entity.getAttempt())
                .startedAt(entity.getStartedAt())
                .endedAt(entity.getEndedAt())
                .errorReason(entity.getErrorReason())
                .build();
    }
}
