package com.bizflow.toolhub.tools;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
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
@Table("tools")
public class ToolEntity {
    @Id
    private UUID id;

    @Column("tool_name")
    private String toolName;

    @Column("version")
    private String version;

    @Column("description")
    private String description;

    @Column("input_schema")
    private String inputSchema;

    @Column("output_schema")
    private String outputSchema;

    @Column("side_effect_level")
    private ToolSideEffectLevel sideEffectLevel;

    @Column("approval_required")
    private boolean approvalRequired;

    @Column("created_at")
    private Instant createdAt;
}
