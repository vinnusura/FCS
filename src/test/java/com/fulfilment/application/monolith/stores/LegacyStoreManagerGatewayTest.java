package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LegacyStoreManagerGatewayTest {

    @Inject
    LegacyStoreManagerGateway gateway;

    @Test
    void testCreateStoreOnLegacySystem() {
        Store store = new Store();
        store.name = "LegacyStore";
        store.quantityProductsInStock = 10;

        // We mainly verify no exception is thrown and coverage is hit
        // since the method writes to a temp file and deletes it.
        assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    }

    @Test
    void testUpdateStoreOnLegacySystem() {
        Store store = new Store();
        store.name = "LegacyStoreUpdate";
        store.quantityProductsInStock = 20;

        assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
    }
}
