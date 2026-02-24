package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import static com.fulfilment.application.monolith.exceptions.ErrorRule.WAREHOUSE_NOT_FOUND;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  @Transactional
  public void archive(String businessUnitCode) {
    Warehouse warehouse = warehouseStore.findById(businessUnitCode);
    if (warehouse == null) {
      throw new WarehouseException(WAREHOUSE_NOT_FOUND);
    }
    warehouse.archivedAt = java.time.LocalDateTime.now();
    warehouseStore.update(warehouse);
  }
}
