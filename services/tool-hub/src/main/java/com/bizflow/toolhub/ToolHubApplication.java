package com.bizflow.toolhub;

import com.bizflow.shared.contracts.config.ReactivePostgresConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ReactivePostgresConfiguration.class)
public class ToolHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToolHubApplication.class, args);
    }
}
