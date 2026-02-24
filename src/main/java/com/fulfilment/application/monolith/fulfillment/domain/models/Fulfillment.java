package com.fulfilment.application.monolith.fulfillment.domain.models;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.validation.constraints.NotNull;

public class Fulfillment {
    @NotNull(message = "Store cannot be null")
    public Store store;

    @NotNull(message = "Product cannot be null")
    public Product product;

    @NotNull(message = "Warehouse cannot be null")
    public Warehouse warehouse;
}
