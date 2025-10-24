package com.go5u.foodflowplatform.inventory.infrastructure.messaging;

import com.go5u.foodflowplatform.inventory.domain.model.events.OrderEvent;
import com.go5u.foodflowplatform.inventory.domain.services.ProductCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductCommandService productCommandService;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(
            topics = "orders-events",
            groupId = "inventory-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: {} with status: {}", event.getOrderId(), event.getStatus());

        try {
            switch(event.getStatus()) {
                case "CREATED":
                    log.info("Processing order creation: {}", event.getOrderId());
                    event.getItems().forEach(item -> {
                        log.info("Reducing inventory for product: {} by quantity: {}",
                                item.getProductId(), item.getQuantity());
                        productCommandService.decreaseInventoryQuantity(
                                item.getProductId(),
                                item.getQuantity()
                        );
                    });
                    break;

                case "CANCELLED":
                    log.info("Processing order cancellation: {}", event.getOrderId());
                    event.getItems().forEach(item -> {
                        log.info("Restoring inventory for product: {} by quantity: {}",
                                item.getProductId(), item.getQuantity());
                        productCommandService.increaseInventoryQuantity(
                                item.getProductId(),
                                item.getQuantity()
                        );
                    });
                    break;

                default:
                    log.warn("Unknown order status: {}", event.getStatus());
            }
        } catch (Exception ex) {
            log.error("Error processing order event: {}", event.getOrderId(), ex);
        }
    }
}