package com.bizflow.gateway.tasks;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, UUID> {
}
