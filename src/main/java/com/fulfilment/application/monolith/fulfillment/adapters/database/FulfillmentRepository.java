package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class FulfillmentRepository implements FulfillmentStore, PanacheRepository<DbFulfillment> {

    @Inject
    WarehouseRepository warehouseRepository;

    @Inject
    EntityManager entityManager;

    @Override
    public void create(Fulfillment fulfillment) {
        // We need the DbWarehouse entity to persist DbFulfillment
        // Ideally domain model should hold enough info, but here we need relational
        // mapping.
        // Since we are inside adapter, we can use other adapters or repositories.
        DbWarehouse dbWarehouse = warehouseRepository.find("businessUnitCode", fulfillment.warehouse.businessUnitCode)
                .firstResult();
        if (dbWarehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse not found for persistence: " + fulfillment.warehouse.businessUnitCode);
        }

        DbFulfillment entity = DbFulfillment.fromDomain(fulfillment, dbWarehouse);
        entity.persist();
    }

    @Override
    public long countByStoreAndProductAndWarehouse(Store store, Product product, Warehouse warehouse) {
        DbWarehouse dbWarehouse = warehouseRepository.find("businessUnitCode", warehouse.businessUnitCode)
                .firstResult();
        if (dbWarehouse == null)
            return 0;

        return DbFulfillment.count("store = ?1 and product = ?2 and warehouse = ?3", store, product, dbWarehouse);
    }

    @Override
    public long countByStoreAndProduct(Store store, Product product) {
        return DbFulfillment.count("store = ?1 and product = ?2", store, product);
    }

    @Override
    public long countByStoreAndWarehouse(Store store, Warehouse warehouse) {
        DbWarehouse dbWarehouse = warehouseRepository.find("businessUnitCode", warehouse.businessUnitCode)
                .firstResult();
        if (dbWarehouse == null)
            return 0;

        return DbFulfillment.count("store = ?1 and warehouse = ?2", store, dbWarehouse);
    }

    @Override
    public long countDistinctWarehousesByStore(Store store) {
        return entityManager.createQuery(
                "SELECT COUNT(DISTINCT f.warehouse) FROM DbFulfillment f WHERE f.store = :store", Long.class)
                .setParameter("store", store)
                .getSingleResult();
    }

    @Override
    public long countByWarehouseAndProduct(Warehouse warehouse, Product product) {
        DbWarehouse dbWarehouse = warehouseRepository.find("businessUnitCode", warehouse.businessUnitCode)
                .firstResult();
        if (dbWarehouse == null)
            return 0;
        return DbFulfillment.count("warehouse = ?1 and product = ?2", dbWarehouse, product);
    }

    @Override
    public long countDistinctProductsByWarehouse(Warehouse warehouse) {
        DbWarehouse dbWarehouse = warehouseRepository.find("businessUnitCode", warehouse.businessUnitCode)
                .firstResult();
        if (dbWarehouse == null)
            return 0;

        return entityManager.createQuery(
                "SELECT COUNT(DISTINCT f.product) FROM DbFulfillment f WHERE f.warehouse = :warehouse", Long.class)
                .setParameter("warehouse", dbWarehouse)
                .getSingleResult();
    }
}
