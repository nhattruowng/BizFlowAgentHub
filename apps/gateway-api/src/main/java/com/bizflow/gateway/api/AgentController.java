package com.bizflow.gateway.api;

import com.bizflow.gateway.agents.AgentEntity;
import com.bizflow.gateway.agents.AgentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {
    private final AgentRepository repository;

    public AgentController(AgentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<AgentEntity>> list() {
        return ResponseEntity.ok(repository.findAll());
    }
}
