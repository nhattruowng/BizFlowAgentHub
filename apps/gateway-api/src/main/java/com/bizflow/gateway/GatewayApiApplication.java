package com.bizflow.gateway;

import com.bizflow.shared.contracts.config.ReactivePostgresConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@Import(ReactivePostgresConfiguration.class)
public class GatewayApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApiApplication.class, args);
    }
}
