package com.bizflow.toolhub.tools;

import com.bizflow.shared.contracts.ToolSideEffectLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "toolhub.seed.enabled", havingValue = "true", matchIfMissing = true)
public class ToolRegistrySeeder implements CommandLineRunner {
    private final ToolRepository toolRepository;

    @Override
    public void run(String... args) {
        toolRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(unused -> Flux.just(
                        tool("send_email_draft", ToolSideEffectLevel.WRITE, false),
                        tool("create_ticket", ToolSideEffectLevel.WRITE, false),
                        tool("read_policy_docs", ToolSideEffectLevel.READ, false),
                        tool("db_query_readonly", ToolSideEffectLevel.READ, false),
                        tool("extract_invoice_mock", ToolSideEffectLevel.READ, false),
                        tool("submit_approval_request", ToolSideEffectLevel.WRITE, true),
                        tool("notify_user", ToolSideEffectLevel.WRITE, false)
                ))
                .flatMap(toolRepository::save)
                .then()
                .block();
    }

    private ToolEntity tool(String name, ToolSideEffectLevel level, boolean approvalRequired) {
        return ToolEntity.builder()
                .toolName(name)
                .version("v1")
                .description("Mock tool for " + name)
                .inputSchema("{\"type\":\"object\"}")
                .outputSchema("{\"type\":\"object\"}")
                .sideEffectLevel(level)
                .approvalRequired(approvalRequired)
                .createdAt(Instant.now())
                .build();
    }
}
