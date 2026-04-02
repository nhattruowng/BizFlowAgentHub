package com.bizflow.approval.core;

import com.bizflow.shared.contracts.ApprovalStatus;
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
@Table("approvals")
public class ApprovalEntity {
    @Id
    private UUID id;

    @Column("workflow_run_id")
    private String workflowRunId;

    @Column("requested_by")
    private String requestedBy;

    @Column("reason")
    private String reason;

    @Column("status")
    private ApprovalStatus status;

    @Column("decided_by")
    private String decidedBy;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
