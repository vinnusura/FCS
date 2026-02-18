package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(String businessUnitCode) {
    Warehouse warehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse " + businessUnitCode + " not found.");
    }

    if (warehouse.archivedAt == null) {
      warehouse.archivedAt = java.time.LocalDateTime.now();
      warehouseStore.update(warehouse);
    }
  }
}
