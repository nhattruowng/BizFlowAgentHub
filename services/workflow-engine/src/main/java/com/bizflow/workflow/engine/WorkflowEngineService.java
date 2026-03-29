package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowRunResponse;
import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.workflow.api.WorkflowRunRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkflowEngineService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository runRepository;
    private final WorkflowStepRepository stepRepository;

    public WorkflowEngineService(WorkflowRepository workflowRepository,
                                 WorkflowRunRepository runRepository,
                                 WorkflowStepRepository stepRepository) {
        this.workflowRepository = workflowRepository;
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
    }

    @Transactional
    public WorkflowRunResponse start(WorkflowRunRequest request) {
        WorkflowEntity workflow = workflowRepository.findByName(request.getWorkflowName())
                .orElseGet(() -> createWorkflow(request.getWorkflowName()));

        WorkflowRunEntity run = new WorkflowRunEntity();
        run.setWorkflowId(workflow.getId());
        run.setTaskId(request.getTaskId());
        run.setCorrelationId(request.getCorrelationId());
        run.setStatus(WorkflowStatus.QUEUED);
        run = runRepository.save(run);

        List<WorkflowStepEntity> steps = buildSteps(run.getId(), workflow.getName());
        stepRepository.saveAll(steps);

        WorkflowStatus finalStatus = determineFinalStatus(workflow.getName());
        run.setStatus(finalStatus);
        runRepository.save(run);

        WorkflowRunResponse response = new WorkflowRunResponse();
        response.setRunId(run.getId().toString());
        response.setWorkflowName(workflow.getName());
        response.setStatus(run.getStatus());
        response.setCorrelationId(run.getCorrelationId());
        response.setStartedAt(run.getStartedAt());
        response.setUpdatedAt(run.getUpdatedAt());
        response.setSteps(steps.stream().map(WorkflowStepEntity::getStepName).toList());
        return response;
    }

    public WorkflowRunResponse get(UUID runId) {
        WorkflowRunEntity run = runRepository.findById(runId).orElseThrow();
        List<WorkflowStepEntity> steps = stepRepository.findByRunId(run.getId());
        WorkflowEntity workflow = workflowRepository.findById(run.getWorkflowId()).orElseThrow();

        WorkflowRunResponse response = new WorkflowRunResponse();
        response.setRunId(run.getId().toString());
        response.setWorkflowName(workflow.getName());
        response.setStatus(run.getStatus());
        response.setCorrelationId(run.getCorrelationId());
        response.setStartedAt(run.getStartedAt());
        response.setUpdatedAt(run.getUpdatedAt());
        response.setSteps(steps.stream().map(WorkflowStepEntity::getStepName).toList());
        return response;
    }

    private WorkflowEntity createWorkflow(String name) {
        WorkflowEntity workflow = new WorkflowEntity();
        workflow.setName(name);
        workflow.setDescription("Auto-registered workflow for " + name);
        return workflowRepository.save(workflow);
    }

    private List<WorkflowStepEntity> buildSteps(UUID runId, String workflowName) {
        List<String> stepNames = switch (workflowName) {
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

        List<WorkflowStepEntity> steps = new ArrayList<>();
        Instant now = Instant.now();
        for (int i = 0; i < stepNames.size(); i++) {
            WorkflowStepEntity step = new WorkflowStepEntity();
            step.setRunId(runId);
            step.setStepName(stepNames.get(i));
            step.setAttempt(1);
            step.setStartedAt(now);
            step.setEndedAt(now);
            step.setStatus(statusForStep(stepNames.get(i), workflowName));
            steps.add(step);
        }
        return steps;
    }

    private WorkflowStatus statusForStep(String stepName, String workflowName) {
        if (workflowName.equals("invoice-approval-workflow") && stepName.equals("WAITING_APPROVAL")) {
            return WorkflowStatus.WAITING_APPROVAL;
        }
        return WorkflowStatus.COMPLETED;
    }

    private WorkflowStatus determineFinalStatus(String workflowName) {
        if (workflowName.equals("invoice-approval-workflow")) {
            return WorkflowStatus.WAITING_APPROVAL;
        }
        return WorkflowStatus.COMPLETED;
    }
}
