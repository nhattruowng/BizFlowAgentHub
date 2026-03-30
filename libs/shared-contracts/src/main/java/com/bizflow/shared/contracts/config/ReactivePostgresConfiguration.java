package com.bizflow.shared.contracts.config;

import com.bizflow.shared.contracts.ApprovalStatus;
import com.bizflow.shared.contracts.TaskStatus;
import com.bizflow.shared.contracts.ToolSideEffectLevel;
import com.bizflow.shared.contracts.WorkflowStatus;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.Option;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class ReactivePostgresConfiguration {

    @Bean
    ConnectionFactoryOptionsBuilderCustomizer reactivePostgresCustomizer() {
        return builder -> {
            builder.option(Option.valueOf("fetchSize"), 256);
            builder.option(Option.valueOf("extensions"), List.of(
                    EnumCodec.builder()
                            .withEnum("task_status", TaskStatus.class)
                            .withEnum("workflow_status", WorkflowStatus.class)
                            .withEnum("approval_status", ApprovalStatus.class)
                            .withEnum("tool_side_effect", ToolSideEffectLevel.class)
                            .build()
            ));
            builder.option(PostgresqlConnectionFactoryProvider.OPTIONS, Map.of(
                    "lock_timeout", "10s",
                    "statement_timeout", "15s"
            ));
        };
    }
}
