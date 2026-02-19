package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfillmentService {

    @Inject
    ProductRepository productRepository;

    @Inject
    WarehouseRepository warehouseRepository;

    @Inject
    EntityManager entityManager;

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

        DbWarehouse warehouse = warehouseRepository.find("businessUnitCode", warehouseBuCode).firstResult();
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found: " + warehouseBuCode);
        }

        // Check if association already exists
        long existingCount = Fulfillment.count("store = ?1 and product = ?2 and warehouse = ?3", store, product,
                warehouse);
        if (existingCount > 0) {
            return; // Already exists, idempotent
        }

        // Constraint 1: Each Product can be fulfilled by a maximum of 2 different
        // Warehouses per Store
        long warehousesForProductAndStore = Fulfillment.count("store = ?1 and product = ?2", store, product);
        if (warehousesForProductAndStore >= 2) {
            throw new IllegalArgumentException(
                    "Product " + productId + " is already fulfilled by 2 warehouses for store " + storeId);
        }

        // Constraint 2: Each Store can be fulfilled by a maximum of 3 different
        // Warehouses
        // Check if this warehouse is already fulfilling for this store (for ANY
        // product)
        long isWarehouseAlreadyFulfillingForStore = Fulfillment.count("store = ?1 and warehouse = ?2", store,
                warehouse);
        if (isWarehouseAlreadyFulfillingForStore == 0) {
            // New warehouse for this store, check limit
            Long distinctWarehousesForStore = entityManager.createQuery(
                    "SELECT COUNT(DISTINCT f.warehouse) FROM Fulfillment f WHERE f.store = :store", Long.class)
                    .setParameter("store", store)
                    .getSingleResult();

            if (distinctWarehousesForStore >= 3) {
                throw new IllegalArgumentException(
                        "Store " + storeId + " is already fulfilled by 3 different warehouses");
            }
        }

        // Constraint 3: Each Warehouse can store maximally 5 types of Products
        // Check if this product is already stored in this warehouse (for ANY store)
        long isProductAlreadyInWarehouse = Fulfillment.count("warehouse = ?1 and product = ?2", warehouse, product);
        if (isProductAlreadyInWarehouse == 0) {
            // New product for this warehouse, check limit
            Long distinctProductsInWarehouse = entityManager.createQuery(
                    "SELECT COUNT(DISTINCT f.product) FROM Fulfillment f WHERE f.warehouse = :warehouse", Long.class)
                    .setParameter("warehouse", warehouse)
                    .getSingleResult();

            if (distinctProductsInWarehouse >= 5) {
                throw new IllegalArgumentException(
                        "Warehouse " + warehouseBuCode + " already stores 5 types of products");
            }
        }

        Fulfillment fulfillment = new Fulfillment();
        fulfillment.store = store;
        fulfillment.product = product;
        fulfillment.warehouse = warehouse;
        fulfillment.persist();
    }
}
