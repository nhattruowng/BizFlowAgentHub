package com.bizflow.toolhub.api;

import com.bizflow.toolhub.tools.ToolEntity;
import com.bizflow.toolhub.tools.ToolHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {
    private final ToolHubService toolHubService;

    public ToolController(ToolHubService toolHubService) {
        this.toolHubService = toolHubService;
    }

    @GetMapping
    public ResponseEntity<List<ToolEntity>> list() {
        return ResponseEntity.ok(toolHubService.listTools());
    }

    @PostMapping("/{toolName}/invoke")
    public ResponseEntity<ToolInvokeResponse> invoke(@PathVariable String toolName,
                                                     @RequestBody ToolInvokeRequest request) {
        return ResponseEntity.ok(toolHubService.invoke(toolName, request));
    }
}
