package com.fulfilment.application.monolith.warehouses.domain.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Warehouse {

  // unique identifier
  @NotBlank(message = "Business Unit Code cannot be blank")
  public String businessUnitCode;

  @NotBlank(message = "Location cannot be blank")
  public String location;

  @NotNull(message = "Capacity cannot be null")
  @Min(value = 1, message = "Capacity must be greater than 0")
  public Integer capacity;

  @NotNull(message = "Stock cannot be null")
  @Min(value = 0, message = "Stock cannot be negative")
  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;
}
