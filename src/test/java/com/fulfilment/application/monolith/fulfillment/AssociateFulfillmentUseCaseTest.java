package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfillment.adapters.database.DbFulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.AssociateFulfillmentUseCase;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class AssociateFulfillmentUseCaseTest {

    @Inject
    AssociateFulfillmentUseCase associateFulfillmentUseCase;

    @Inject
    ProductRepository productRepository;

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    void testAssociation_Success() {
        Store store = createStore("Store1");
        Product product = createProduct("Prod1");
        DbWarehouse warehouse = createWarehouse("WH1");

        assertDoesNotThrow(
                () -> associateFulfillmentUseCase.associate(store.id, product.id, warehouse.businessUnitCode));

        assertEquals(1,
                DbFulfillment.count("store = ?1 and product = ?2 and warehouse = ?3", store, product, warehouse));
    }

    @Test
    void testAssociation_Max2WarehousesPerProductPerStore() {
        Store store = createStore("Store2");
        Product product = createProduct("Prod2");
        DbWarehouse wh1 = createWarehouse("WH2");
        DbWarehouse wh2 = createWarehouse("WH3");
        DbWarehouse wh3 = createWarehouse("WH4");

        associateFulfillmentUseCase.associate(store.id, product.id, wh1.businessUnitCode);
        associateFulfillmentUseCase.associate(store.id, product.id, wh2.businessUnitCode);

        assertThrows(IllegalArgumentException.class, () -> {
            associateFulfillmentUseCase.associate(store.id, product.id, wh3.businessUnitCode);
        });
    }

    @Transactional
    Store createStore(String name) {
        Store store = new Store();
        store.name = name;
        store.quantityProductsInStock = 100;
        store.persist();
        return store;
    }

    @Transactional
    Product createProduct(String name) {
        Product product = new Product();
        product.name = name;
        product.price = BigDecimal.TEN;
        product.stock = 10;
        productRepository.persist(product);
        return product;
    }

    @Transactional
    DbWarehouse createWarehouse(String buCode) {
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
