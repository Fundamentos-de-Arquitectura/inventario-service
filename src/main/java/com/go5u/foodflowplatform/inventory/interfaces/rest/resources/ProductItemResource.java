package com.go5u.foodflowplatform.inventory.interfaces.rest.resources;

import com.go5u.foodflowplatform.inventory.domain.model.valueobjects.ExpirationDate;
import com.go5u.foodflowplatform.inventory.domain.model.valueobjects.Price;
import com.go5u.foodflowplatform.inventory.domain.model.valueobjects.Quantity;

public record ProductItemResource(Long productItemId,
                                  Quantity quantity,
                                  ExpirationDate expirationDate,
                                  Price price) {
}
