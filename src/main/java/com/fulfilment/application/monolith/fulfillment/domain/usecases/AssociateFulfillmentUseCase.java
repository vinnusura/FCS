package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AssociateFulfillmentUseCase implements AssociateFulfillmentOperation {

    private final FulfillmentStore fulfillmentStore;
    private final ProductRepository productRepository;
    private final WarehouseStore warehouseStore;

    public AssociateFulfillmentUseCase(FulfillmentStore fulfillmentStore,
            ProductRepository productRepository,
            WarehouseStore warehouseStore) {
        this.fulfillmentStore = fulfillmentStore;
        this.productRepository = productRepository;
        this.warehouseStore = warehouseStore;
    }

    @Override
    @Transactional
    public void associate(Long storeId, Long productId, String warehouseBuCode) {
        Store store = Store.findById(storeId);
        if (store == null) {
            throw new IllegalArgumentException("Store not found: " + storeId);
        }

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        Warehouse warehouse = warehouseStore.findById(warehouseBuCode);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found: " + warehouseBuCode);
        }

        // Check if association already exists
        long existingCount = fulfillmentStore.countByStoreAndProductAndWarehouse(store, product, warehouse);
        if (existingCount > 0) {
            return; // Already exists, idempotent
        }

        // Constraint 1: Each Product can be fulfilled by a maximum of 2 different
        // Warehouses per Store
        long warehousesForProductAndStore = fulfillmentStore.countByStoreAndProduct(store, product);
        if (warehousesForProductAndStore >= 2) {
            throw new IllegalArgumentException(
                    "Product " + productId + " is already fulfilled by 2 warehouses for store " + storeId);
        }

        // Constraint 2: Each Store can be fulfilled by a maximum of 3 different
        // Warehouses
        long isWarehouseAlreadyFulfillingForStore = fulfillmentStore.countByStoreAndWarehouse(store, warehouse);
        if (isWarehouseAlreadyFulfillingForStore == 0) {
            // New warehouse for this store, check limit
            long distinctWarehousesForStore = fulfillmentStore.countDistinctWarehousesByStore(store);

            if (distinctWarehousesForStore >= 3) {
                throw new IllegalArgumentException(
                        "Store " + storeId + " is already fulfilled by 3 different warehouses");
            }
        }

        // Constraint 3: Each Warehouse can store maximally 5 types of Products
        long isProductAlreadyInWarehouse = fulfillmentStore.countByWarehouseAndProduct(warehouse, product);
        if (isProductAlreadyInWarehouse == 0) {
            // New product for this warehouse, check limit
            long distinctProductsInWarehouse = fulfillmentStore.countDistinctProductsByWarehouse(warehouse);

            if (distinctProductsInWarehouse >= 5) {
                throw new IllegalArgumentException(
                        "Warehouse " + warehouseBuCode + " already stores 5 types of products");
            }
        }

        Fulfillment fulfillment = new Fulfillment();
        fulfillment.store = store;
        fulfillment.product = product;
        fulfillment.warehouse = warehouse;
        fulfillmentStore.create(fulfillment);
    }
}
