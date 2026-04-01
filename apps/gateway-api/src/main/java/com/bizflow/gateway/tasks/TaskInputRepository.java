package com.bizflow.gateway.tasks;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TaskInputRepository extends ReactiveCrudRepository<TaskInputEntity, UUID> {
    @Query("select id, task_id, payload_json, created_at "
            + "from task_inputs "
            + "where task_id = :taskId "
            + "order by created_at desc "
            + "limit 1")
    Mono<TaskInputEntity> findLatestByTaskId(@Param("taskId") UUID taskId);
}
