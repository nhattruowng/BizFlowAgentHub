package com.bizflow.workflow.events;

import com.bizflow.shared.events.OutboxEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private static final TypeReference<Map<String, Object>> PAYLOAD_TYPE = new TypeReference<>() {
    };

    private final KafkaTemplate<String, OutboxEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxRelayProperties relayProperties;

    public Mono<Void> publish(OutboxEventEntity entity) {
        return Mono.fromFuture(kafkaTemplate.send(
                relayProperties.getTopic(),
                entity.getAggregateId(),
                toMessage(entity)
        )).then();
    }

    private OutboxEvent toMessage(OutboxEventEntity entity) {
        OutboxEvent event = new OutboxEvent();
        event.setId(entity.getId().toString());
        event.setAggregateType(entity.getAggregateType());
        event.setAggregateId(entity.getAggregateId());
        event.setEventType(entity.getEventType());
        event.setPayload(deserializePayload(entity.getPayload()));
        event.setCreatedAt(entity.getCreatedAt());
        return event;
    }

    private Map<String, Object> deserializePayload(String payload) {
        try {
            return objectMapper.readValue(payload, PAYLOAD_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize outbox payload for Kafka publish", exception);
        }
    }
}
