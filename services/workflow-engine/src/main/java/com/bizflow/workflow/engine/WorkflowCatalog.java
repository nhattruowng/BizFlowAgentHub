package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowStatus;
import com.bizflow.workflow.api.WorkflowDefinitionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class WorkflowCatalog {
    private final Map<String, WorkflowBlueprint> blueprints = new LinkedHashMap<>();

    public WorkflowCatalog() {
        register(new WorkflowBlueprint(
                "email-ticket-workflow",
                "Email and support ticket automation flow",
                WorkflowStatus.COMPLETED,
                List.of(
                        new WorkflowStepPlan("ROUTER_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("PLANNER_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("EXECUTOR_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("VALIDATOR_AGENT", WorkflowStatus.COMPLETED)
                )
        ));
        register(new WorkflowBlueprint(
                "invoice-approval-workflow",
                "Invoice OCR, policy review and approval gate",
                WorkflowStatus.WAITING_APPROVAL,
                List.of(
                        new WorkflowStepPlan("ROUTER_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("CONTEXT_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("POLICY_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("WAITING_APPROVAL", WorkflowStatus.WAITING_APPROVAL)
                )
        ));
        register(new WorkflowBlueprint(
                "policy-lookup-workflow",
                "Knowledge retrieval and validation flow",
                WorkflowStatus.COMPLETED,
                List.of(
                        new WorkflowStepPlan("ROUTER_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("KNOWLEDGE_AGENT", WorkflowStatus.COMPLETED),
                        new WorkflowStepPlan("VALIDATOR_AGENT", WorkflowStatus.COMPLETED)
                )
        ));
    }

    public Flux<WorkflowDefinitionResponse> listDefinitions() {
        return Flux.fromIterable(blueprints.values())
                .map(blueprint -> WorkflowDefinitionResponse.builder()
                        .workflowName(blueprint.getName())
                        .description(blueprint.getDescription())
                        .terminalStatus(blueprint.getTerminalStatus())
                        .steps(blueprint.stepNames())
                        .build());
    }

    public WorkflowBlueprint getRequired(String workflowName) {
        WorkflowBlueprint blueprint = blueprints.get(workflowName);
        if (blueprint == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow is not registered: " + workflowName);
        }
        return blueprint;
    }

    private void register(WorkflowBlueprint blueprint) {
        blueprints.put(blueprint.getName(), blueprint);
    }

    @Getter
    @RequiredArgsConstructor
    public static final class WorkflowBlueprint {
        private final String name;
        private final String description;
        private final WorkflowStatus terminalStatus;
        private final List<WorkflowStepPlan> steps;

        public List<String> stepNames() {
            return steps.stream().map(WorkflowStepPlan::stepName).toList();
        }
    }

    public record WorkflowStepPlan(String stepName, WorkflowStatus status) {
    }
}
