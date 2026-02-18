package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.warehouse.api.beans.Warehouse;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WarehouseEndpointIT {

  @Test
  @Order(1)
  public void testCreateWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.setBusinessUnitCode("TEST-BU-001");
    warehouse.setLocation("ZWOLLE-001");
    warehouse.setCapacity(100);
    warehouse.setStock(50);

    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo("TEST-BU-001"))
        .body("location", equalTo("ZWOLLE-001"));
  }

  @Test
  @Order(2)
  public void testCreateDuplicateWarehouseFails() {
    Warehouse warehouse = new Warehouse();
    warehouse.setBusinessUnitCode("TEST-BU-001");
    warehouse.setLocation("ZWOLLE-001");
    warehouse.setCapacity(100);
    warehouse.setStock(50);

    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(3)
  public void testReplaceWarehouse() {
    Warehouse newWarehouse = new Warehouse();
    newWarehouse.setBusinessUnitCode("TEST-BU-002");
    newWarehouse.setLocation("ZWOLLE-001");
    newWarehouse.setCapacity(100);
    newWarehouse.setStock(50); // Stock matches old one

    given()
        .contentType(ContentType.JSON)
        .body(newWarehouse)
        .when()
        .put("/warehouse/TEST-BU-001")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo("TEST-BU-002"));
  }

  @Test
  @Order(4)
  public void testArchiveWarehouse() {
    given()
        .when()
        .delete("/warehouse/TEST-BU-002")
        .then()
        .statusCode(204);
  }

  @Test
  @Order(5)
  public void testArchiveNonExistentWarehouse() {
    given()
        .when()
        .delete("/warehouse/NON-EXISTENT")
        .then()
        .statusCode(404);
  }
}
