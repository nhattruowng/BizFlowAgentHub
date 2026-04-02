package com.bizflow.audit.api;

import com.bizflow.audit.core.AuditLogEntity;
import com.bizflow.audit.core.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping
    public Flux<AuditLogEntity> list(@RequestParam(required = false) String workflowRunId,
                                     @RequestParam(required = false) String action) {
        return auditService.list(workflowRunId, action);
    }

    @GetMapping("/{runId}")
    public Flux<AuditLogEntity> getByRun(@PathVariable String runId) {
        return auditService.list(runId, null);
    }

    @PostMapping
    public Mono<AuditLogEntity> append(@Valid @RequestBody AuditLogRequest request) {
        return auditService.append(request);
    }
}
