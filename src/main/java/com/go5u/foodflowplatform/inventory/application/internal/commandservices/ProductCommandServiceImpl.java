package com.go5u.foodflowplatform.inventory.application.internal.commandservices;

import com.go5u.foodflowplatform.inventory.domain.model.aggregates.Product;
import com.go5u.foodflowplatform.inventory.domain.model.commands.CreateProductCommand;
import com.go5u.foodflowplatform.inventory.domain.model.events.InventoryEvent;
import com.go5u.foodflowplatform.inventory.domain.services.ProductCommandService;
import com.go5u.foodflowplatform.inventory.infrastructure.messaging.InventoryEventProducer;
import com.go5u.foodflowplatform.inventory.infrastructure.persistence.jpa.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final InventoryEventProducer inventoryEventProducer;

    public ProductCommandServiceImpl(ProductRepository productRepository,
                                     InventoryEventProducer inventoryEventProducer) {
        this.productRepository = productRepository;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    @Override
    public Long handle(CreateProductCommand command) {
        var product = new Product(command);
        try {
            productRepository.save(product);
            publishInventoryStatus(product);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving product: " + e.getMessage());
        }
        return product.getProductId();
    }

    @Override
    public void decreaseInventoryQuantity(Long productId, Integer quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        int currentQuantity = product.getQuantity().quantity();
        int newQuantity = currentQuantity - quantity;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient inventory for product: " + productId);
        }

        product.decreaseQuantity(quantity);

        productRepository.save(product);
        publishInventoryStatus(product);
        log.info("Decreased inventory for product {}: {} -> {}", productId, currentQuantity, newQuantity);
    }

    @Override
    public void increaseInventoryQuantity(Long productId, Integer quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        int currentQuantity = product.getQuantity().quantity();
        int newQuantity = currentQuantity + quantity;

        product.increaseQuantity(quantity);

        productRepository.save(product);
        publishInventoryStatus(product);
        log.info("Increased inventory for product {}: {} -> {}", productId, currentQuantity, newQuantity);
    }

    private void publishInventoryStatus(Product product) {
        String status;
        int quantity = product.getQuantity().quantity();

        if (quantity <= 0) {
            status = "OUT_OF_STOCK";
        } else if (quantity <= 5) {
            status = "LOW_STOCK";
        } else {
            status = "AVAILABLE";
        }

        InventoryEvent event = new InventoryEvent(
                product.getProductId(),
                product.getName(),
                quantity,
                status,
                null
        );

        inventoryEventProducer.publishInventoryEvent(event);
    }
}