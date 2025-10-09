package com.go5u.foodflowplatform.inventory.interfaces.rest.transform;

import com.go5u.foodflowplatform.inventory.domain.model.aggregates.Product;
import com.go5u.foodflowplatform.inventory.interfaces.rest.resources.ProductItemResource;

public class ProductItemResourceFromEntityAssembler {
    public static ProductItemResource toResourceFromEntity(Product entity){
        return new ProductItemResource(
                entity.getProductId(),
                entity.getQuantity(),
                entity.getExpirationDate(),
                entity.getPrice());
    }
}
