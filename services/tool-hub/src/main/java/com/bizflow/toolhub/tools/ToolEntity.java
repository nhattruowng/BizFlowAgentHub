package com.bizflow.toolhub.tools;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tools")
public class ToolEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "tool_name", nullable = false, unique = true)
    private String toolName;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "input_schema", columnDefinition = "text", nullable = false)
    private String inputSchema;

    @Column(name = "output_schema", columnDefinition = "text", nullable = false)
    private String outputSchema;

    @Enumerated(EnumType.STRING)
    @Column(name = "side_effect_level", nullable = false)
    private ToolSideEffectLevel sideEffectLevel;

    @Column(name = "approval_required", nullable = false)
    private boolean approvalRequired;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputSchema() {
        return inputSchema;
    }

    public void setInputSchema(String inputSchema) {
        this.inputSchema = inputSchema;
    }

    public String getOutputSchema() {
        return outputSchema;
    }

    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    public ToolSideEffectLevel getSideEffectLevel() {
        return sideEffectLevel;
    }

    public void setSideEffectLevel(ToolSideEffectLevel sideEffectLevel) {
        this.sideEffectLevel = sideEffectLevel;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
