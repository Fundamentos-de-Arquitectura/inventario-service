package com.go5u.foodflowplatform.inventory.domain.services;

import com.go5u.foodflowplatform.inventory.domain.model.commands.CreateProductCommand;

public interface ProductCommandService {

    Long handle(CreateProductCommand command);

}
