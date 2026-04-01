package com.bizflow.shared.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepView {
    private String stepName;
    private WorkflowStatus status;
    private int attempt;
    private Instant startedAt;
    private Instant endedAt;
    private String errorReason;
}
