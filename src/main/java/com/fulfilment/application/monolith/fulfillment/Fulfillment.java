package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import jakarta.validation.constraints.NotNull;

@Entity
public class Fulfillment extends PanacheEntity {

    @NotNull(message = "Store cannot be null")
    @ManyToOne
    public Store store;

    @NotNull(message = "Product cannot be null")
    @ManyToOne
    public Product product;

    @NotNull(message = "Warehouse cannot be null")
    @ManyToOne
    public DbWarehouse warehouse;
}
