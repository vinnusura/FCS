package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "fulfillment")
public class DbFulfillment extends PanacheEntity {

    @NotNull(message = "Store cannot be null")
    @ManyToOne
    public Store store;

    @NotNull(message = "Product cannot be null")
    @ManyToOne
    public Product product;

    @NotNull(message = "Warehouse cannot be null")
    @ManyToOne
    public DbWarehouse warehouse;

    public Fulfillment toDomain() {
        Fulfillment domain = new Fulfillment();
        domain.store = this.store;
        domain.product = this.product;
        domain.warehouse = this.warehouse.toWarehouse();
        return domain;
    }

    public static DbFulfillment fromDomain(Fulfillment domain, DbWarehouse dbWarehouse) {
        DbFulfillment entity = new DbFulfillment();
        entity.store = domain.store;
        entity.product = domain.product;
        entity.warehouse = dbWarehouse;
        return entity;
    }
}
