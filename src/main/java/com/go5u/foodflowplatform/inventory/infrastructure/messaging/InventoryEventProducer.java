package com.go5u.foodflowplatform.inventory.infrastructure.messaging;

import com.go5u.foodflowplatform.inventory.domain.model.events.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventProducer {

    @Value("${kafka.topic.inventory-events:inventory-events}")
    private String inventoryTopicName;

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public void publishInventoryEvent(InventoryEvent event) {
        event.setTimestamp(LocalDateTime.now());

        Message<InventoryEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, inventoryTopicName)
                // some Spring Kafka versions don't expose MESSAGE_KEY constant; use literal header name
                .setHeader("kafka_messageKey", event.getProductId().toString())
                .setHeader("event-type", event.getStatus())
                .build();

        // send returns a (deprecated) ListenableFuture; keep it in a local var and bridge to CompletableFuture
        Object sendResult = kafkaTemplate.send(message);

        // If modern Spring Kafka returns a CompletableFuture
        if (sendResult instanceof CompletableFuture<?> jf) {
            @SuppressWarnings("unchecked")
            CompletableFuture<SendResult<String, InventoryEvent>> cf =
                    (CompletableFuture<SendResult<String, InventoryEvent>>) jf;
            cf.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish inventory event for product: {}", event.getProductId(), ex);
                } else {
                    log.info("Successfully published inventory event for product: {} with status: {}",
                            event.getProductId(), event.getStatus());
                }
            });
            return;
        }

        // Fallback: treat as a java.util.concurrent.Future (covers older ListenableFuture implementations as well)
        if (sendResult instanceof java.util.concurrent.Future<?> fut) {
            CompletableFuture.runAsync(() -> {
                try {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.Future<SendResult<String, InventoryEvent>> f =
                            (java.util.concurrent.Future<SendResult<String, InventoryEvent>>) fut;
                    f.get();
                    log.info("Successfully published inventory event for product: {} with status: {}",
                            event.getProductId(), event.getStatus());
                } catch (Exception ex) {
                    log.error("Failed to publish inventory event for product: {}", event.getProductId(), ex);
                }
            });
            return;
        }

        // Unknown type: log and return
        log.warn("Unexpected send() return type: {}", sendResult == null ? "null" : sendResult.getClass().getName());
    }
}