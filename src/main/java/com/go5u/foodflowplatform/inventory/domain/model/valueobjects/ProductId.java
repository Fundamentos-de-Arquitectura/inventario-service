package com.go5u.foodflowplatform.inventory.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ProductId(Long productId) {

    public ProductId {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number.");
        }
    }
}
