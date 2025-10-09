package com.go5u.foodflowplatform.inventory.domain.services;

import com.go5u.foodflowplatform.inventory.domain.model.aggregates.Product;
import com.go5u.foodflowplatform.inventory.domain.model.queries.GetAllProductsQuery;
import com.go5u.foodflowplatform.inventory.domain.model.queries.GetProductByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProductQueryService {
    List<Product> handle(GetAllProductsQuery query);
    Optional<Product> handle(GetProductByIdQuery query);
}
