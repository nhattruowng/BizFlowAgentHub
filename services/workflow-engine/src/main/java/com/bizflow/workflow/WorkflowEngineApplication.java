package com.bizflow.workflow;

import com.bizflow.shared.contracts.config.ReactivePostgresConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ReactivePostgresConfiguration.class)
public class WorkflowEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowEngineApplication.class, args);
    }
}
