package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Fulfillment extends PanacheEntity {

    @ManyToOne
    public Store store;

    @ManyToOne
    public Product product;

    @ManyToOne
    public DbWarehouse warehouse;
}
