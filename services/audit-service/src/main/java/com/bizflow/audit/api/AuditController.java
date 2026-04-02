package com.bizflow.audit.api;

import com.bizflow.audit.core.AuditLogEntity;
import com.bizflow.audit.core.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/{runId}")
    public Flux<AuditLogEntity> getByRun(@PathVariable String runId) {
        return auditService.findByRunId(runId);
    }
}
