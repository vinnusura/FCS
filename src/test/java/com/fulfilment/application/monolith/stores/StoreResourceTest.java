package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreResourceTest {

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Test
    void testStoreLifecycle() {
        // Mock the legacy gateway
        doNothing().when(legacyStoreManagerGateway).createStoreOnLegacySystem(any());
        doNothing().when(legacyStoreManagerGateway).updateStoreOnLegacySystem(any());

        // Create
        Store store = new Store();
        store.name = "Integration Store";
        store.quantityProductsInStock = 50;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(store)
                .when().post("/store")
                .then()
                .statusCode(201)
                .body("name", is("Integration Store"))
                .extract().path("id");

        // Get
        given()
                .when().get("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Integration Store"));

        // Update (PUT)
        store.name = "Integration Store Updated";
        store.quantityProductsInStock = 60;
        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when().put("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Integration Store Updated"));

        // Patch
        Store patchStore = new Store();
        patchStore.quantityProductsInStock = 70;
        given()
                .contentType(ContentType.JSON)
                .body(patchStore)
                .when().patch("/store/" + id)
                .then()
                .statusCode(200)
                .body("quantityProductsInStock", is(70));

        // Delete
        given()
                .when().delete("/store/" + id)
                .then()
                .statusCode(204);

        // Verify Deleted
        given()
                .when().get("/store/" + id)
                .then()
                .statusCode(404);
    }
}
