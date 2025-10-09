package com.go5u.foodflowplatform.inventory.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record ExpirationDate(LocalDate expirationDate) {
    public ExpirationDate {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
    }
}
