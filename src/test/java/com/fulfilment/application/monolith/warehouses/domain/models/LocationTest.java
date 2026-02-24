package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void testLocationRecord() {
        Location location = new Location("LOC_A", 10, 500);

        assertEquals("LOC_A", location.identification);
        assertEquals(10, location.maxNumberOfWarehouses);
        assertEquals(500, location.maxCapacity);

        assertNotNull(location.toString());
    }
}
