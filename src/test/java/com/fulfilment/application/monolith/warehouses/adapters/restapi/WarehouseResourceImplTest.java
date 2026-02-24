package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import com.fulfilment.application.monolith.exceptions.WarehouseException;
import static com.fulfilment.application.monolith.exceptions.ErrorRule.WAREHOUSE_NOT_FOUND;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private CreateWarehouseOperation createWarehouseOperation;
    @Mock
    private ReplaceWarehouseOperation replaceWarehouseOperation;
    @Mock
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    @InjectMocks
    private WarehouseResourceImpl resource;

    @Test
    void createANewWarehouseUnit_Success() {
        com.warehouse.api.beans.Warehouse input = new com.warehouse.api.beans.Warehouse();
        input.setBusinessUnitCode("BU1");
        input.setLocation("LOC1");
        input.setCapacity(100);
        input.setStock(10);

        Warehouse domainWarehouse = new Warehouse();
        domainWarehouse.businessUnitCode = "BU1";
        domainWarehouse.location = "LOC1";
        domainWarehouse.capacity = 100;
        domainWarehouse.stock = 10;

        when(warehouseRepository.findById("BU1")).thenReturn(domainWarehouse);

        com.warehouse.api.beans.Warehouse result = resource.createANewWarehouseUnit(input);

        assertNotNull(result);
        assertEquals("BU1", result.getBusinessUnitCode());
        verify(createWarehouseOperation).create(any(Warehouse.class));
    }

    @Test
    void createANewWarehouseUnit_Fail() {
        com.warehouse.api.beans.Warehouse input = new com.warehouse.api.beans.Warehouse();
        input.setBusinessUnitCode("BU1");

        doThrow(new IllegalArgumentException("Invalid")).when(createWarehouseOperation).create(any());

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> resource.createANewWarehouseUnit(input));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void listAllWarehousesUnits() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU1";
        when(warehouseRepository.getAll()).thenReturn(List.of(w));

        List<com.warehouse.api.beans.Warehouse> result = resource.listAllWarehousesUnits();

        assertEquals(1, result.size());
        assertEquals("BU1", result.get(0).getBusinessUnitCode());
    }

    @Test
    void getAWarehouseUnitByID_Success() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU1";
        when(warehouseRepository.findById("BU1")).thenReturn(w);

        com.warehouse.api.beans.Warehouse result = resource.getAWarehouseUnitByID("BU1");

        assertNotNull(result);
        assertEquals("BU1", result.getBusinessUnitCode());
    }

    @Test
    void getAWarehouseUnitByID_NotFound() {
        when(warehouseRepository.findById("BU1")).thenReturn(null);

        assertThrows(jakarta.ws.rs.NotFoundException.class, () -> resource.getAWarehouseUnitByID("BU1"));
    }

    @Test
    void archiveAWarehouseUnitByID_Success() {
        resource.archiveAWarehouseUnitByID("BU1");
    }

    @Test
    void archiveAWarehouseUnitByID_NotFound() {
        doThrow(new WarehouseException(WAREHOUSE_NOT_FOUND)).when(archiveWarehouseOperation).archive("BU1");

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> resource.archiveAWarehouseUnitByID("BU1"));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void archiveAWarehouseUnitByID_BadRequest() {
        doThrow(new IllegalArgumentException("bad")).when(archiveWarehouseOperation).archive("BU1");

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> resource.archiveAWarehouseUnitByID("BU1"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void replaceTheCurrentActiveWarehouse_Success() {
        com.warehouse.api.beans.Warehouse input = new com.warehouse.api.beans.Warehouse();
        input.setBusinessUnitCode("BU2");

        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU2";
        when(warehouseRepository.findById("BU2")).thenReturn(w);

        com.warehouse.api.beans.Warehouse result = resource.replaceTheCurrentActiveWarehouse("BU1", input);

        assertNotNull(result);
        verify(replaceWarehouseOperation).replace(any());
    }

    @Test
    void replaceTheCurrentActiveWarehouse_NotFound() {
        com.warehouse.api.beans.Warehouse input = new com.warehouse.api.beans.Warehouse();
        doThrow(new WarehouseException(WAREHOUSE_NOT_FOUND)).when(replaceWarehouseOperation).replace(any());

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> resource.replaceTheCurrentActiveWarehouse("BU1", input));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }
}
