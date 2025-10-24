package com.go5u.foodflowplatform.inventory.domain.services;

import com.go5u.foodflowplatform.inventory.domain.model.commands.CreateProductCommand;

public interface ProductCommandService {

    Long handle(CreateProductCommand command);

    void decreaseInventoryQuantity(Long productId, Integer quantity);

    void increaseInventoryQuantity(Long productId, Integer quantity);
}
