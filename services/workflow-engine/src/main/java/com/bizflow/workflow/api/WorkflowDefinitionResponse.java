package com.bizflow.workflow.api;

import com.bizflow.shared.contracts.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDefinitionResponse {
    private String workflowName;
    private String description;
    private WorkflowStatus terminalStatus;
    private List<String> steps;
}
