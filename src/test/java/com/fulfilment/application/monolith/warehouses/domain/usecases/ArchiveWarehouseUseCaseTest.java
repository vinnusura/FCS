package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.exceptions.WarehouseException;

@QuarkusTest
public class ArchiveWarehouseUseCaseTest {

    @InjectMock
    WarehouseStore warehouseStore;

    @Inject
    ArchiveWarehouseUseCase useCase;

    @Test
    void archive_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";

        when(warehouseStore.findById("BU1")).thenReturn(warehouse);

        useCase.archive("BU1");

        verify(warehouseStore).update(warehouse);
    }

    @Test
    void archive_Fails_NotFound() {
        when(warehouseStore.findById("BU1")).thenReturn(null);

        assertThrows(WarehouseException.class, () -> useCase.archive("BU1"));
    }
}
