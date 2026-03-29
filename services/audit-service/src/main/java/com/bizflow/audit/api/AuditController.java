package com.bizflow.audit.api;

import com.bizflow.audit.core.AuditLogEntity;
import com.bizflow.audit.core.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/{runId}")
    public ResponseEntity<List<AuditLogEntity>> getByRun(@PathVariable String runId) {
        return ResponseEntity.ok(auditService.findByRunId(runId));
    }
}
