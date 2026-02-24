package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface FulfillmentStore {
    void create(Fulfillment fulfillment);

    long countByStoreAndProductAndWarehouse(Store store, Product product, Warehouse warehouse);

    long countByStoreAndProduct(Store store, Product product);

    long countByStoreAndWarehouse(Store store, Warehouse warehouse);

    long countDistinctWarehousesByStore(Store store);

    long countByWarehouseAndProduct(Warehouse warehouse, Product product);

    long countDistinctProductsByWarehouse(Warehouse warehouse);
}
