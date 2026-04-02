package com.bizflow.workflow.events;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_PUBLISHED = "PUBLISHED";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final OutboxRelayProperties relayProperties;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(fixedDelayString = "${bizflow.kafka.relay.fixed-delay:2000}")
    public void relayPending() {
        if (!relayProperties.getRelay().isEnabled()) {
            return;
        }
        if (!running.compareAndSet(false, true)) {
            return;
        }
        relayBatch()
                .doFinally(signalType -> running.set(false))
                .subscribe();
    }

    Mono<Void> relayBatch() {
        return Flux.concat(
                        outboxEventRepository.findByStatusOrderByCreatedAtAsc(WorkflowOutboxService.STATUS_NEW),
                        outboxEventRepository.findByStatusOrderByCreatedAtAsc(STATUS_FAILED)
                )
                .take(relayProperties.getRelay().getBatchSize())
                .concatMap(this::publishAndMark)
                .then();
    }

    private Mono<OutboxEventEntity> publishAndMark(OutboxEventEntity event) {
        return kafkaEventPublisher.publish(event)
                .then(markPublished(event))
                .onErrorResume(exception -> markFailed(event, exception));
    }

    private Mono<OutboxEventEntity> markPublished(OutboxEventEntity event) {
        return outboxEventRepository.save(event.toBuilder()
                .status(STATUS_PUBLISHED)
                .publishedAt(Instant.now())
                .lastError(null)
                .build());
    }

    private Mono<OutboxEventEntity> markFailed(OutboxEventEntity event, Throwable throwable) {
        return outboxEventRepository.save(event.toBuilder()
                .status(STATUS_FAILED)
                .lastError(limitError(throwable))
                .build());
    }

    private String limitError(Throwable throwable) {
        String message = throwable == null ? "Unknown Kafka publish error" : throwable.getMessage();
        if (message == null || message.length() <= 500) {
            return message;
        }
        return message.substring(0, 500);
    }
}
