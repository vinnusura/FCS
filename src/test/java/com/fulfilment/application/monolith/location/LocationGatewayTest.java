package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationGatewayTest {

  private final LocationGateway locationGateway = new LocationGateway();

  @ParameterizedTest
  @ValueSource(strings = {
      "ZWOLLE-001", "ZWOLLE-002",
      "AMSTERDAM-001", "AMSTERDAM-002",
      "TILBURG-001", "HELMOND-001",
      "EINDHOVEN-001", "VETSBY-001"
  })
  void testResolveExistingLocations(String identifier) {
    Location location = locationGateway.resolveByIdentifier(identifier);
    assertNotNull(location);
    assertEquals(identifier, location.identification);
  }

  @Test
  void testResolveNonExistingLocationShouldReturnNull() {
    Location location = locationGateway.resolveByIdentifier("NON-EXISTING");
    assertNull(location);
  }
}
