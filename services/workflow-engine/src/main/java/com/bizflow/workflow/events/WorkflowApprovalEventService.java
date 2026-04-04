package com.bizflow.workflow.events;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.shared.contracts.WorkflowStepView;
import com.bizflow.shared.events.OutboxEvent;
import com.bizflow.workflow.engine.WorkflowEntity;
import com.bizflow.workflow.engine.WorkflowRepository;
import com.bizflow.workflow.engine.WorkflowRunEntity;
import com.bizflow.workflow.engine.WorkflowRunRepository;
import com.bizflow.workflow.engine.WorkflowStepEntity;
import com.bizflow.workflow.engine.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowApprovalEventService {
    private final WorkflowRunRepository runRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowOutboxService workflowOutboxService;

    public Mono<Void> applyApprovalDecision(OutboxEvent event) {
        String workflowRunId = valueAsString(event.getPayload(), "workflowRunId");
        if (!StringUtils.hasText(workflowRunId)) {
            return Mono.empty();
        }

        UUID runId;
        try {
            runId = UUID.fromString(workflowRunId);
        } catch (IllegalArgumentException exception) {
            return Mono.empty();
        }

        WorkflowStatus targetStatus = "approval.approved".equals(event.getEventType())
                ? WorkflowStatus.APPROVED
                : WorkflowStatus.REJECTED;

        return runRepository.findById(runId)
                .flatMap(run -> stepRepository.findByRunId(runId)
                        .collectList()
                        .flatMap(steps -> updateRunAndSteps(run, steps, targetStatus, event)))
                .then();
    }

    private Mono<Void> updateRunAndSteps(WorkflowRunEntity run,
                                         List<WorkflowStepEntity> steps,
                                         WorkflowStatus targetStatus,
                                         OutboxEvent event) {
        Instant now = Instant.now();
        WorkflowRunEntity updatedRun = run.toBuilder()
                .status(targetStatus)
                .errorReason(targetStatus == WorkflowStatus.REJECTED ? valueAsString(event.getPayload(), "reason") : null)
                .updatedAt(now)
                .build();

        List<WorkflowStepEntity> updatedSteps = steps.stream()
                .map(step -> toUpdatedStep(step, targetStatus, now))
                .toList();

        return runRepository.save(updatedRun)
                .thenMany(stepRepository.saveAll(updatedSteps))
                .then(workflowRepository.findById(run.getWorkflowId()))
                .flatMap(workflow -> emitWorkflowUpdatedEvent(updatedRun, workflow, updatedSteps, event));
    }

    private WorkflowStepEntity toUpdatedStep(WorkflowStepEntity step, WorkflowStatus targetStatus, Instant now) {
        if (step.getStatus() != WorkflowStatus.WAITING_APPROVAL && !"WAITING_APPROVAL".equals(step.getStepName())) {
            return step;
        }
        return step.toBuilder()
                .status(targetStatus)
                .endedAt(now)
                .errorReason(targetStatus == WorkflowStatus.REJECTED ? "Approval rejected" : null)
                .build();
    }

    private Mono<Void> emitWorkflowUpdatedEvent(WorkflowRunEntity run,
                                                WorkflowEntity workflow,
                                                List<WorkflowStepEntity> steps,
                                                OutboxEvent approvalEvent) {
        WorkflowRunResponse response = mapResponse(run, workflow.getName(), steps);
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("approvalEventId", approvalEvent.getId());
        metadata.put("approvalId", approvalEvent.getAggregateId());
        metadata.put("decisionStatus", approvalEvent.getEventType());
        metadata.put("decisionBy", valueAsString(approvalEvent.getPayload(), "decidedBy"));
        metadata.put("decisionReason", valueAsString(approvalEvent.getPayload(), "reason"));
        return workflowOutboxService.appendWorkflowRunUpdatedEvent(response, metadata);
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

    private String resolveCurrentStep(List<WorkflowStepEntity> steps) {
        for (WorkflowStepEntity step : steps) {
            if (!isTerminalStepStatus(step.getStatus())) {
                return step.getStepName();
            }
        }
        return steps.isEmpty() ? null : steps.get(steps.size() - 1).getStepName();
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

    private String valueAsString(Map<String, Object> payload, String key) {
        if (payload == null) {
            return null;
        }
        Object value = payload.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
