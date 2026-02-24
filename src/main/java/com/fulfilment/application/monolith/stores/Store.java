package com.fulfilment.application.monolith.stores;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Cacheable
public class Store extends PanacheEntity {

  @NotBlank(message = "Store name must not be blank")
  @Size(max = 40, message = "Store name must not exceed 40 characters")
  @Column(length = 40, unique = true, nullable = false)
  public String name;

  @NotNull(message = "Quantity must not be null")
  @Min(value = 0, message = "Quantity of products in stock cannot be negative")
  public Integer quantityProductsInStock;

  public Store() {
  }

  public Store(String name) {
    this.name = name;
    this.quantityProductsInStock = 0;
  }
}
