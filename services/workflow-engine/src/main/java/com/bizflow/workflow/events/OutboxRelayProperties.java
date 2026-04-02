package com.bizflow.workflow.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bizflow.kafka")
public class OutboxRelayProperties {
    private String topic = "events";
    private Relay relay = new Relay();

    @Getter
    @Setter
    public static class Relay {
        private boolean enabled = true;
        private int batchSize = 20;
        private long fixedDelay = 2000L;
    }
}
