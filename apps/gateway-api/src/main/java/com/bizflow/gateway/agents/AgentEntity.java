package com.bizflow.gateway.agents;

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
@Table("agents")
public class AgentEntity {
    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("version")
    private String version;

    @Column("created_at")
    private Instant createdAt;
}
