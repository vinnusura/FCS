package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

@QuarkusTest
public class CreateWarehouseUseCaseTest {

    @InjectMock
    WarehouseStore warehouseStore;

    @InjectMock
    LocationResolver locationResolver;

    @Inject
    CreateWarehouseUseCase useCase;

    @Test
    void create_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";
        warehouse.location = "LOC1";
        warehouse.capacity = 100;
        warehouse.stock = 50;

        Location location = new Location("LOC1", 5, 500);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.findById("BU1")).thenReturn(null);
        when(warehouseStore.getByLocation("LOC1")).thenReturn(Collections.emptyList());

        useCase.create(warehouse);

        verify(warehouseStore).create(warehouse);
    }

    @Test
    void create_Fails_BuCodeExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";

        when(warehouseStore.findById("BU1")).thenReturn(new Warehouse());

        assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_Fails_InvalidLocation() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";
        warehouse.location = "INVALID";

        when(warehouseStore.findById("BU1")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void testCreate_FailMaxWarehousesPerLocationPolicy() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MW1";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        // Mock location resolver to return a location with max 1 warehouse
        Location location = new Location("ZWOLLE-001", 1, 100);
        when(locationResolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(location);

        // Mock warehouse store to return 1 existing warehouse for this location
        when(warehouseStore.getByLocation("ZWOLLE-001")).thenReturn(List.of(new Warehouse()));

        // Expect exception
        assertThrows(IllegalStateException.class,
                () -> useCase.create(warehouse));
    }

    @Test
    void create_Fails_MaxWarehousesInLocation() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";
        warehouse.location = "LOC1";
        warehouse.capacity = 10;
        warehouse.stock = 5;

        Location location = new Location("LOC1", 1, 500);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.findById("BU1")).thenReturn(null);
        when(warehouseStore.getByLocation("LOC1")).thenReturn(Collections.singletonList(new Warehouse()));

        assertThrows(IllegalStateException.class, () -> useCase.create(warehouse));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_Fails_MaxCapacityExceeded() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";
        warehouse.location = "LOC1";
        warehouse.capacity = 60;
        warehouse.stock = 10;

        Location location = new Location("LOC1", 5, 100);
        Warehouse existing = new Warehouse();
        existing.capacity = 50;

        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.findById("BU1")).thenReturn(null);
        when(warehouseStore.getByLocation("LOC1")).thenReturn(Collections.singletonList(existing));

        assertThrows(IllegalStateException.class, () -> useCase.create(warehouse));
        verify(warehouseStore, never()).create(any());
    }
}
