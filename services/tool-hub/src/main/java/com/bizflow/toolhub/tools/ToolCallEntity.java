package com.bizflow.toolhub.tools;

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
@Table("tool_calls")
public class ToolCallEntity {
    @Id
    private UUID id;

    @Column("tool_id")
    private UUID toolId;

    @Column("workflow_run_id")
    private String workflowRunId;

    @Column("status")
    private String status;

    @Column("request_payload")
    private String requestPayload;

    @Column("response_payload")
    private String responsePayload;

    @Column("created_at")
    private Instant createdAt;
}
