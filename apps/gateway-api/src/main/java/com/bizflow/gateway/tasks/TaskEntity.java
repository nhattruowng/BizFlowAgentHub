package com.bizflow.gateway.tasks;

import com.bizflow.shared.contracts.TaskStatus;
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
@Table("tasks")
public class TaskEntity {
    @Id
    private UUID id;

    @Column("tenant_id")
    private String tenantId;

    @Column("source")
    private String source;

    @Column("type")
    private String type;

    @Column("status")
    private TaskStatus status;

    @Column("workflow_run_id")
    private String workflowRunId;

    @Column("correlation_id")
    private String correlationId;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
