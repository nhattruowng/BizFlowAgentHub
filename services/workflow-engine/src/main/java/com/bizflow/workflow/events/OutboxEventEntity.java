package com.bizflow.workflow.events;

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
@Table("events_outbox")
public class OutboxEventEntity {
    @Id
    private UUID id;

    @Column("aggregate_type")
    private String aggregateType;

    @Column("aggregate_id")
    private String aggregateId;

    @Column("event_type")
    private String eventType;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("published_at")
    private Instant publishedAt;

    @Column("last_error")
    private String lastError;
}
