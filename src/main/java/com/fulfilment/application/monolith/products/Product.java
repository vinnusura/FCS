package com.fulfilment.application.monolith.products;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Cacheable
public class Product {

  @Id
  @GeneratedValue
  public Long id;

  @NotBlank(message = "Name cannot be blank")
  @Size(max = 40, message = "Name must not exceed 40 characters")
  @Column(length = 40, unique = true)
  public String name;

  @Column(nullable = true)
  public String description;

  @NotNull(message = "Price cannot be null")
  @Min(value = 0, message = "Price cannot be negative")
  @Column(precision = 10, scale = 2, nullable = true)
  public BigDecimal price;

  @Min(value = 0, message = "Stock cannot be negative")
  public int stock;

  public Product() {
  }

  public Product(String name) {
    this.name = name;
  }
}
