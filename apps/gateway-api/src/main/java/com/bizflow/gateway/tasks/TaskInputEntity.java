package com.bizflow.gateway.tasks;

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
@Table("task_inputs")
public class TaskInputEntity {
    @Id
    private UUID id;

    @Column("task_id")
    private UUID taskId;

    @Column("payload_json")
    private String payloadJson;

    @Column("created_at")
    private Instant createdAt;
}
