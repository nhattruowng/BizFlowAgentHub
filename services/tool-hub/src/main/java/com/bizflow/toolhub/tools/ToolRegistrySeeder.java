package com.bizflow.toolhub.tools;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ToolRegistrySeeder implements CommandLineRunner {
    private final ToolRepository toolRepository;

    public ToolRegistrySeeder(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @Override
    public void run(String... args) {
        if (toolRepository.count() > 0) {
            return;
        }
        register("send_email_draft", ToolSideEffectLevel.WRITE, false);
        register("create_ticket", ToolSideEffectLevel.WRITE, false);
        register("read_policy_docs", ToolSideEffectLevel.READ, false);
        register("db_query_readonly", ToolSideEffectLevel.READ, false);
        register("extract_invoice_mock", ToolSideEffectLevel.READ, false);
        register("submit_approval_request", ToolSideEffectLevel.WRITE, true);
        register("notify_user", ToolSideEffectLevel.WRITE, false);
    }

    private void register(String name, ToolSideEffectLevel level, boolean approvalRequired) {
        ToolEntity tool = new ToolEntity();
        tool.setToolName(name);
        tool.setVersion("v1");
        tool.setDescription("Mock tool for " + name);
        tool.setInputSchema("{\"type\":\"object\"}");
        tool.setOutputSchema("{\"type\":\"object\"}");
        tool.setSideEffectLevel(level);
        tool.setApprovalRequired(approvalRequired);
        toolRepository.save(tool);
    }
}
