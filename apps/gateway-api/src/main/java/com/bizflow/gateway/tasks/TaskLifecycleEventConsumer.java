package com.bizflow.gateway.tasks;

import com.bizflow.shared.events.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskLifecycleEventConsumer {
    private final TaskService taskService;

    @KafkaListener(topics = "${bizflow.kafka.topic:events}")
    public void consume(OutboxEvent event, Acknowledgment acknowledgment) {
        if (event == null) {
            acknowledgment.acknowledge();
            return;
        }
        String eventType = event.getEventType();
        if (!"workflow.run.created".equals(eventType) && !"workflow.run.updated".equals(eventType)) {
            acknowledgment.acknowledge();
            return;
        }
        taskService.syncFromWorkflowEvent(event).block();
        acknowledgment.acknowledge();
    }
}
