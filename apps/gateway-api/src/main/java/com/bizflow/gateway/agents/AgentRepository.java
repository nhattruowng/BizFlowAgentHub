package com.bizflow.gateway.agents;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AgentRepository extends ReactiveCrudRepository<AgentEntity, UUID> {
}
