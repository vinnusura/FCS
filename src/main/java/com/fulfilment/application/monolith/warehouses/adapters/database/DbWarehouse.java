package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {

  @Id
  @GeneratedValue
  public Long id;

  @NotBlank(message = "Business Unit Code must not be blank")
  @Size(max = 50, message = "Business Unit Code must not exceed 50 characters")
  @Column(unique = true, nullable = false, length = 50)
  public String businessUnitCode;

  @NotBlank(message = "Location must not be blank")
  @Size(max = 100, message = "Location must not exceed 100 characters")
  @Column(nullable = false, length = 100)
  public String location;

  @NotNull(message = "Capacity must not be null")
  @Min(value = 1, message = "Capacity must be at least 1")
  @Column(nullable = false)
  public Integer capacity;

  @NotNull(message = "Stock must not be null")
  @Min(value = 0, message = "Stock cannot be negative")
  @Column(nullable = false)
  public Integer stock;

  @NotNull(message = "Created date must not be null")
  @Column(nullable = false)
  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

  public DbWarehouse() {
  }

  public Warehouse toWarehouse() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = this.businessUnitCode;
    warehouse.location = this.location;
    warehouse.capacity = this.capacity;
    warehouse.stock = this.stock;
    warehouse.createdAt = this.createdAt;
    warehouse.archivedAt = this.archivedAt;
    return warehouse;
  }
}
