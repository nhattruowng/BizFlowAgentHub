package com.bizflow.gateway.tasks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskInputRepository extends JpaRepository<TaskInputEntity, UUID> {
}
