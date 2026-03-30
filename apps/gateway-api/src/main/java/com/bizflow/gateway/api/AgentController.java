package com.bizflow.gateway.api;

import com.bizflow.gateway.agents.AgentEntity;
import com.bizflow.gateway.agents.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {
    private final AgentRepository repository;

    @GetMapping
    public Flux<AgentEntity> list() {
        return repository.findAll();
    }
}
