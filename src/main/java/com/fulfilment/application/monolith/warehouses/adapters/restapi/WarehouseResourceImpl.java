package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private final WarehouseRepository warehouseRepository;
  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;

  @Inject
  public WarehouseResourceImpl(
      WarehouseRepository warehouseRepository,
      CreateWarehouseOperation createWarehouseOperation,
      ReplaceWarehouseOperation replaceWarehouseOperation,
      ArchiveWarehouseOperation archiveWarehouseOperation) {
    this.warehouseRepository = warehouseRepository;
    this.createWarehouseOperation = createWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
  }

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    try {
      var domainWarehouse = toDomain(data);
      createWarehouseOperation.create(domainWarehouse);
      return getAWarehouseUnitByID(domainWarehouse.businessUnitCode);
    } catch (RuntimeException e) {
      throw mapException(e);
    }
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var domainWarehouse = warehouseRepository.findById(id);
    if (domainWarehouse == null) {
      throw new jakarta.ws.rs.NotFoundException("Warehouse " + id + " not found");
    }
    return toWarehouseResponse(domainWarehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    try {
      archiveWarehouseOperation.archive(id);
    } catch (RuntimeException e) {
      throw mapException(e);
    }
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    try {
      var domainWarehouse = toDomain(data);
      replaceWarehouseOperation.replace(domainWarehouse);
      return getAWarehouseUnitByID(domainWarehouse.businessUnitCode);
    } catch (RuntimeException e) {
      throw mapException(e);
    }
  }

  private WebApplicationException mapException(RuntimeException e) {
    if (e instanceof WarehouseException we) {
      if (we.getErrorRule() != null && "WAREHOUSE_NOT_FOUND".equals(we.getErrorRule().name())) {
        return new WebApplicationException("Warehouse not found", Response.Status.NOT_FOUND);
      }
      return new WebApplicationException(we.getMessage(), Response.Status.BAD_REQUEST);
    }
    if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
      if ("Warehouse not found".equals(e.getMessage())) {
        return new WebApplicationException("Warehouse not found", Response.Status.NOT_FOUND);
      }
      return new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
    }
    throw e;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(Warehouse data) {
    var warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    return warehouse;
  }
}
