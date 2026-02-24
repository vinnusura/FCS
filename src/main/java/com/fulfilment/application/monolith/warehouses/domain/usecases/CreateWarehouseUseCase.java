package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    // 1. Business Unit Code Verification
    if (warehouseStore.findById(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException(
          "Warehouse with Business Unit Code " + warehouse.businessUnitCode + " already exists.");
    }

    // 2. Location Validation
    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location " + warehouse.location + " is not a valid location.");
    }

    // 3. Warehouse Creation Feasibility
    var existingWarehouses = warehouseStore.getByLocation(warehouse.location);
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalStateException(
          "Location " + warehouse.location + " has reached its maximum number of warehouses.");
    }

    // 4. Capacity and Stock Validation
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock cannot exceed capacity.");
    }

    int currentUsedCapacity = existingWarehouses.stream().mapToInt(w -> w.capacity).sum();
    if (currentUsedCapacity + warehouse.capacity > location.maxCapacity) {
      throw new IllegalStateException(
          "Location " + warehouse.location + " does not have enough capacity for the new warehouse.");
    }

    // if all went well, create the warehouse
    // Set createdAt if null
    if (warehouse.createdAt == null) {
      warehouse.createdAt = java.time.LocalDateTime.now();
    }
    warehouseStore.create(warehouse);
  }
}
