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
@Table("workflow_runs")
public class WorkflowRunEntity {
    @Id
    private UUID id;

    @Column("workflow_id")
    private UUID workflowId;

    @Column("task_id")
    private String taskId;

    @Column("status")
    private WorkflowStatus status;

    @Column("correlation_id")
    private String correlationId;

    @Column("error_reason")
    private String errorReason;

    @Column("started_at")
    private Instant startedAt;

    @Column("updated_at")
    private Instant updatedAt;
}
