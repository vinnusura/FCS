package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @jakarta.transaction.Transactional
  public void replace(String oldBuCode, Warehouse newWarehouse) {
    // 1. Fetch old warehouse
    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(oldBuCode);
    if (oldWarehouse == null) {
      throw new IllegalArgumentException("Warehouse to replace not found: " + oldBuCode);
    }
    if (oldWarehouse.archivedAt != null) {
      throw new IllegalArgumentException("Warehouse " + oldBuCode + " is already archived.");
    }

    // 2. Additional Validations for Replacement
    // Capacity Accommodation: ensure the new warehouse's capacity can accommodate
    // the stock from the warehouse being replaced
    if (newWarehouse.capacity < oldWarehouse.stock) {
      throw new IllegalArgumentException("New warehouse capacity insufficient for old warehouse stock.");
    }

    // Stock Matching: Confirm that the stock of the new warehouse matches the stock
    // of the previous warehouse
    if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
      throw new IllegalArgumentException("New warehouse stock must match old warehouse stock.");
    }

    // 3. Standard Validations (Validation logic duplicated from
    // CreateWarehouseUseCase for now)
    // 3.1 Business Unit Code Verification
    if (warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException(
          "Warehouse with Business Unit Code " + newWarehouse.businessUnitCode + " already exists.");
    }

    // 3.2 Location Validation
    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location " + newWarehouse.location + " is not a valid location.");
    }

    // 3.3 Warehouse Creation Feasibility & Capacity Validation
    var existingWarehouses = warehouseStore.getByLocation(newWarehouse.location);
    long activeCount = existingWarehouses.stream().filter(w -> w.archivedAt == null).count();

    // If replacing in the SAME location, we don't increment count because old one
    // is archived.
    // If different location, we increment count for new location.
    boolean sameLocation = oldWarehouse.location.equals(newWarehouse.location);

    if (!sameLocation) {
      if (activeCount >= location.maxNumberOfWarehouses) {
        throw new IllegalStateException(
            "Location " + newWarehouse.location + " has reached its maximum number of warehouses.");
      }
    } else {
      // If same location, activeCount includes oldWarehouse. After archiving, count
      // is activeCount - 1.
      // New warehouse adds 1. So total remains same. No check needed for maxNumber if
      // activeCount <= max.
      // But if activeCount > max (inconsistent state), maybe we shouldn't allow?
      // Assuming valid state.
    }

    // Capacity Logic
    int currentUsedCapacity = existingWarehouses.stream()
        .filter(w -> w.archivedAt == null)
        .mapToInt(w -> w.capacity).sum();

    int capacityAfterArchive = currentUsedCapacity;
    if (sameLocation) {
      capacityAfterArchive -= oldWarehouse.capacity;
    }

    if (capacityAfterArchive + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalStateException(
          "Location " + newWarehouse.location + " does not have enough capacity for the new warehouse.");
    }

    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new IllegalArgumentException("Stock cannot exceed capacity.");
    }

    // 4. Perform Replacement
    // Archive old
    oldWarehouse.archivedAt = java.time.LocalDateTime.now(); // or pass archiving time
    warehouseStore.update(oldWarehouse);

    // Create new
    if (newWarehouse.createdAt == null) {
      newWarehouse.createdAt = java.time.LocalDateTime.now();
    }
    warehouseStore.create(newWarehouse);
  }
}
