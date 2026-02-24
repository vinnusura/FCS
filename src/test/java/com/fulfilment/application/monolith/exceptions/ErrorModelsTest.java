package com.fulfilment.application.monolith.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorModelsTest {

    @Test
    void testErrorRule() {
        assertEquals("LOCATION_CAPACITY_EXCEEDED", ErrorRule.LOCATION_CAPACITY_EXCEEDED.name());
        assertEquals("LOCATION_MAX_WAREHOUSES_REACHED",
                ErrorRule.LOCATION_MAX_WAREHOUSES_REACHED.name());
    }

    @Test
    void testWarehouseException() {
        WarehouseException exception = new WarehouseException(ErrorRule.LOCATION_CAPACITY_EXCEEDED);
        assertNotNull(exception);
        assertEquals(ErrorRule.LOCATION_CAPACITY_EXCEEDED, exception.getErrorRule());
    }
}
