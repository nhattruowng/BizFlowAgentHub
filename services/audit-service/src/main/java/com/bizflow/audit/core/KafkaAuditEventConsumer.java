package com.bizflow.audit.core;

import com.bizflow.shared.events.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class KafkaAuditEventConsumer {
    private final AuditService auditService;

    @KafkaListener(topics = "${bizflow.kafka.topic:events}")
    public void consume(OutboxEvent event, Acknowledgment acknowledgment) {
        if (event == null || !StringUtils.hasText(event.getId())) {
            acknowledgment.acknowledge();
            return;
        }
        auditService.appendFromEvent(event).block();
        acknowledgment.acknowledge();
    }
}
