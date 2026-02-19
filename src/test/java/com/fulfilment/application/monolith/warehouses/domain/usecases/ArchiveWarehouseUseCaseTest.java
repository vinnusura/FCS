package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArchiveWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ArchiveWarehouseUseCase(warehouseStore);
    }

    @Test
    void archive_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";

        when(warehouseStore.findByBusinessUnitCode("BU1")).thenReturn(warehouse);

        useCase.archive("BU1");

        verify(warehouseStore).remove(warehouse);
    }

    @Test
    void archive_Fails_NotFound() {
        when(warehouseStore.findByBusinessUnitCode("BU1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.archive("BU1"));
    }
}
