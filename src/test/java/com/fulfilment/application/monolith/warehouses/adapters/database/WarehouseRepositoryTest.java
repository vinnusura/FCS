package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    void testCreateAndFind() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MW1";
        warehouse.location = "New York";
        warehouse.capacity = 100;
        warehouse.stock = 10;
        warehouse.createdAt = LocalDateTime.now();

        warehouseRepository.create(warehouse);

        Warehouse found = warehouseRepository.findById("MW1");
        assertNotNull(found);
        assertEquals("New York", found.location);
        assertEquals(100, found.capacity);
        assertEquals(10, found.stock);
    }

    @Test
    void testUpdate() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MW2";
        warehouse.location = "London";
        warehouse.capacity = 200;
        warehouse.stock = 20;
        warehouse.createdAt = LocalDateTime.now();

        warehouseRepository.create(warehouse);

        Warehouse toUpdate = new Warehouse();
        toUpdate.businessUnitCode = "MW2";
        toUpdate.location = "London Updated";
        toUpdate.capacity = 250;
        toUpdate.stock = 25;
        toUpdate.archivedAt = null;

        warehouseRepository.update(toUpdate);

        Warehouse updated = warehouseRepository.findById("MW2");
        assertEquals("London Updated", updated.location);
        assertEquals(250, updated.capacity);
        assertEquals(25, updated.stock);
    }

    @Test
    void testRemove() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MW3";
        warehouse.location = "Paris";
        warehouse.capacity = 150;
        warehouse.stock = 15;
        warehouse.createdAt = LocalDateTime.now();

        warehouseRepository.create(warehouse);
        assertNotNull(warehouseRepository.findById("MW3"));

        warehouseRepository.remove(warehouse);
        assertNull(warehouseRepository.findById("MW3"));
    }

    @Test
    void testGetByLocation() {
        Warehouse w1 = new Warehouse();
        w1.businessUnitCode = "L1";
        w1.location = "Berlin";
        w1.capacity = 100;
        w1.stock = 10;
        w1.createdAt = LocalDateTime.now();

        Warehouse w2 = new Warehouse();
        w2.businessUnitCode = "L2";
        w2.location = "Berlin";
        w2.capacity = 100;
        w2.stock = 10;
        w2.createdAt = LocalDateTime.now();

        warehouseRepository.create(w1);
        warehouseRepository.create(w2);

        List<Warehouse> berlinWarehouses = warehouseRepository.getByLocation("Berlin");
        assertEquals(2, berlinWarehouses.size());
    }
}
