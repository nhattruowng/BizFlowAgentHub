package com.bizflow.workflow.engine;

import com.bizflow.shared.contracts.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("workflow_steps")
public class WorkflowStepEntity {
    @Id
    private UUID id;

    @Column("run_id")
    private UUID runId;

    @Column("step_name")
    private String stepName;

    @Column("status")
    private WorkflowStatus status;

    @Column("attempt")
    private int attempt;

    @Column("started_at")
    private Instant startedAt;

    @Column("ended_at")
    private Instant endedAt;

    @Column("error_reason")
    private String errorReason;
}
