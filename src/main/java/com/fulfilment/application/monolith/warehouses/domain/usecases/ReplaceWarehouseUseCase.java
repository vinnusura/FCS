package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import static com.fulfilment.application.monolith.exceptions.ErrorRule.*;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore,
      ArchiveWarehouseOperation archiveWarehouseOperation,
      LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    Warehouse existing = fetchExistingWarehouse(newWarehouse.businessUnitCode);

    validateReplacementEligibility(existing, newWarehouse);
    validateLocationCapacity(existing, newWarehouse);

    archiveWarehouseOperation.archive(existing.businessUnitCode);
    createNewWarehouse(newWarehouse);
  }

  private Warehouse fetchExistingWarehouse(String buCode) {
    Warehouse existing = warehouseStore.findById(buCode);
    if (existing == null) {
      throw new WarehouseException(WAREHOUSE_NOT_FOUND, "Warehouse not found for BU code " + buCode);
    }
    return existing;
  }

  private void validateReplacementEligibility(Warehouse existing, Warehouse newWarehouse) {
    if (existing.archivedAt != null) {
      throw new WarehouseException(WAREHOUSE_ALREADY_ARCHIVED);
    }
    if (!existing.location.equals(newWarehouse.location)) {
      throw new WarehouseException(LOCATION_CHANGE_NOT_ALLOWED);
    }
    if (!existing.stock.equals(newWarehouse.stock)) {
      throw new WarehouseException(WAREHOUSE_STOCK_MISMATCH);
    }
    if (newWarehouse.capacity < existing.stock) {
      throw new WarehouseException(WAREHOUSE_CAPACITY_NOT_ENOUGH);
    }
    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new WarehouseException(WAREHOUSE_STOCK_EXCEEDS_CAPACITY);
    }
  }

  private void validateLocationCapacity(Warehouse existing, Warehouse newWarehouse) {
    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new WarehouseException(INVALID_LOCATION);
    }

    var warehousesAtLocation = warehouseStore.getByLocation(newWarehouse.location);

    int usedCapacity = warehousesAtLocation.stream()
        .filter(w -> w.archivedAt == null)
        .mapToInt(w -> w.capacity)
        .sum();

    int adjustedCapacity = usedCapacity - existing.capacity;

    if (adjustedCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new WarehouseException(LOCATION_CAPACITY_EXCEEDED);
    }
  }

  private void createNewWarehouse(Warehouse newWarehouse) {
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
