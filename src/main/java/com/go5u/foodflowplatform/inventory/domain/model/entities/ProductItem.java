package com.go5u.foodflowplatform.inventory.domain.model.entities;

import com.go4u.keepitfreshplatform.inventory.domain.model.aggregates.Product;
import com.go4u.keepitfreshplatform.inventory.domain.model.valueobjects.ExpirationDate;
import com.go4u.keepitfreshplatform.inventory.domain.model.valueobjects.Price;
import com.go4u.keepitfreshplatform.inventory.domain.model.valueobjects.Quantity;
import com.go4u.keepitfreshplatform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class ProductItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private ExpirationDate expirationDate;

    @NotNull
    private Quantity quantity;

    @NotNull
    private Price price;

    public ProductItem() {
        // Default constructor for JPA
    }

    public ProductItem(Product product, ExpirationDate expirationDate, Quantity quantity, Price price) {
        this.product = product;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductItem(Product product, ExpirationDate expirationDate, Quantity quantity) {
        super();
    }
}
