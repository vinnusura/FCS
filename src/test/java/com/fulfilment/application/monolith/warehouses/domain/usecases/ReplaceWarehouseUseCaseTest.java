package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ReplaceWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;
    @Mock
    private LocationResolver locationResolver;

    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReplaceWarehouseUseCase(warehouseStore, locationResolver);
    }

    @Test
    void replace_Success() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.businessUnitCode = "OLD";
        oldWarehouse.stock = 100;
        oldWarehouse.location = "LOC1";
        oldWarehouse.capacity = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "NEW";
        newWarehouse.location = "LOC1";
        newWarehouse.capacity = 100;
        newWarehouse.stock = 100;

        Location location = new Location("LOC1", 5, 200);

        when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldWarehouse);
        when(warehouseStore.findByBusinessUnitCode("NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.getByLocation("LOC1")).thenReturn(Collections.singletonList(oldWarehouse));

        useCase.replace("OLD", newWarehouse);

        verify(warehouseStore).update(oldWarehouse);
        verify(warehouseStore).create(newWarehouse);
        assertNotNull(oldWarehouse.archivedAt);
    }

    @Test
    void replace_Fails_OldNotFound() {
        when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace("OLD", new Warehouse()));
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replace_Fails_CapacityInsufficient() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.stock = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.capacity = 99;

        when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldWarehouse);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace("OLD", newWarehouse));
    }

    @Test
    void replace_Fails_StockMismatch() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.stock = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.capacity = 100;
        newWarehouse.stock = 99;

        when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldWarehouse);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace("OLD", newWarehouse));
    }
}
