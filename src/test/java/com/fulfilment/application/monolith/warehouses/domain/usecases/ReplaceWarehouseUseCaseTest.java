package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
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
    @Mock
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReplaceWarehouseUseCase(warehouseStore, archiveWarehouseOperation, locationResolver);
    }

    @Test
    void replace_Success() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.businessUnitCode = "OLD";
        oldWarehouse.stock = 100;
        oldWarehouse.location = "LOC1";
        oldWarehouse.capacity = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "OLD";
        newWarehouse.location = "LOC1";
        newWarehouse.capacity = 100;
        newWarehouse.stock = 100;

        Location location = new Location("LOC1", 5, 200);

        when(warehouseStore.findById("OLD")).thenReturn(oldWarehouse);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.getByLocation("LOC1")).thenReturn(Collections.singletonList(oldWarehouse));

        useCase.replace(newWarehouse);

        verify(archiveWarehouseOperation).archive("OLD");
        verify(warehouseStore).create(newWarehouse);
        assertNull(oldWarehouse.archivedAt); // The use case doesn't modify the object explicitly, it calls archive op
    }

    @Test
    void replace_Fails_OldNotFound() {
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "OLD";
        when(warehouseStore.findById("OLD")).thenReturn(null);

        assertThrows(WarehouseException.class, () -> useCase.replace(newWarehouse));
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replace_Fails_CapacityInsufficient() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.businessUnitCode = "OLD";
        oldWarehouse.stock = 100;
        oldWarehouse.location = "LOC1";
        oldWarehouse.capacity = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "OLD";
        newWarehouse.capacity = 99;
        newWarehouse.location = "LOC1";
        newWarehouse.stock = 100; // Match existing stock

        when(warehouseStore.findById("OLD")).thenReturn(oldWarehouse);

        assertThrows(WarehouseException.class, () -> useCase.replace(newWarehouse));
    }

    @Test
    void replace_Fails_StockMismatch() {
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.businessUnitCode = "OLD";
        oldWarehouse.stock = 100;
        oldWarehouse.location = "LOC1";
        oldWarehouse.capacity = 100;

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "OLD";
        newWarehouse.capacity = 100;
        newWarehouse.stock = 99; // Mismatched stock
        newWarehouse.location = "LOC1";

        when(warehouseStore.findById("OLD")).thenReturn(oldWarehouse);

        assertThrows(WarehouseException.class, () -> useCase.replace(newWarehouse));
    }
}
