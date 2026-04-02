package com.bizflow.audit.core;

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
@Table("audit_logs")
public class AuditLogEntity {
    @Id
    private UUID id;

    @Column("workflow_run_id")
    private String workflowRunId;

    @Column("action")
    private String action;

    @Column("payload")
    private String payload;

    @Column("created_at")
    private Instant createdAt;
}
