package com.bizflow.workflow.events;

import com.bizflow.shared.events.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowApprovalEventConsumer {
    private final WorkflowApprovalEventService workflowApprovalEventService;

    @KafkaListener(topics = "${bizflow.kafka.topic:events}")
    public void consume(OutboxEvent event, Acknowledgment acknowledgment) {
        if (event == null) {
            acknowledgment.acknowledge();
            return;
        }
        String eventType = event.getEventType();
        if (!"approval.approved".equals(eventType) && !"approval.rejected".equals(eventType)) {
            acknowledgment.acknowledge();
            return;
        }
        workflowApprovalEventService.applyApprovalDecision(event).block();
        acknowledgment.acknowledge();
    }
}
