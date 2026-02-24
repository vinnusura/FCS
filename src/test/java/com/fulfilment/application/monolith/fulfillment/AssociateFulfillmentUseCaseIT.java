package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.fulfillment.adapters.database.DbFulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.AssociateFulfillmentUseCase;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AssociateFulfillmentUseCaseIT {

    @Inject
    AssociateFulfillmentUseCase associateFulfillmentUseCase;

    @Inject
    ProductRepository productRepository;

    @Inject
    WarehouseRepository warehouseRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        DbFulfillment.deleteAll();
        Store.deleteAll();
        productRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testSuccess() {
        Store s1 = new Store("S1");
        s1.persist();
        Product p1 = new Product("P1");
        productRepository.persist(p1);
        createWarehouse("W1");

        associateFulfillmentUseCase.associate(s1.id, p1.id, "W1");

        Assertions.assertEquals(1, DbFulfillment.count());
    }

    @Test
    @Transactional
    public void testMax2WarehousesPerProductPerStore() {
        Store s1 = new Store("S1");
        s1.persist();
        Product p1 = new Product("P1");
        productRepository.persist(p1);
        createWarehouse("W1");
        createWarehouse("W2");
        createWarehouse("W3");

        associateFulfillmentUseCase.associate(s1.id, p1.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p1.id, "W2");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            associateFulfillmentUseCase.associate(s1.id, p1.id, "W3");
        });
    }

    @Test
    @Transactional
    public void testMax3WarehousesPerStore() {
        Store s1 = new Store("S1");
        s1.persist();
        Product p1 = new Product("P1");
        productRepository.persist(p1);
        Product p2 = new Product("P2");
        productRepository.persist(p2);
        Product p3 = new Product("P3");
        productRepository.persist(p3);
        Product p4 = new Product("P4");
        productRepository.persist(p4);

        createWarehouse("W1");
        createWarehouse("W2");
        createWarehouse("W3");
        createWarehouse("W4");

        associateFulfillmentUseCase.associate(s1.id, p1.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p2.id, "W2");
        associateFulfillmentUseCase.associate(s1.id, p3.id, "W3");

        // Adding W4 (new warehouse for store S1) -> Fail
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            associateFulfillmentUseCase.associate(s1.id, p4.id, "W4");
        });

        // Adding W1 again (existing warehouse for store S1) -> Success
        associateFulfillmentUseCase.associate(s1.id, p4.id, "W1");
    }

    @Test
    @Transactional
    public void testMax5ProductsPerWarehouse() {
        Store s1 = new Store("S1");
        s1.persist();
        Product p1 = new Product("P1");
        productRepository.persist(p1);
        Product p2 = new Product("P2");
        productRepository.persist(p2);
        Product p3 = new Product("P3");
        productRepository.persist(p3);
        Product p4 = new Product("P4");
        productRepository.persist(p4);
        Product p5 = new Product("P5");
        productRepository.persist(p5);
        Product p6 = new Product("P6");
        productRepository.persist(p6);

        createWarehouse("W1");

        associateFulfillmentUseCase.associate(s1.id, p1.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p2.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p3.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p4.id, "W1");
        associateFulfillmentUseCase.associate(s1.id, p5.id, "W1");

        // Adding P6 (new product for warehouse W1) -> Fail
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            associateFulfillmentUseCase.associate(s1.id, p6.id, "W1");
        });

        // Adding P1 again (existing product for warehouse W1) -> Success (Idempotent)
        associateFulfillmentUseCase.associate(s1.id, p1.id, "W1");
    }

    private DbWarehouse createWarehouse(String buCode) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = buCode;
        w.location = "Loc-" + buCode;
        w.capacity = 1000;
        w.stock = 0;
        w.createdAt = LocalDateTime.now();
        warehouseRepository.create(w);
        return warehouseRepository.find("businessUnitCode", buCode).firstResult();
    }
}
