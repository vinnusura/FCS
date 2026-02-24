package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductResourceTest {

        @Test
        void testProductLifecycle() {
                // Create
                Product product = new Product();
                product.name = "Lifecycle Product";
                product.description = "Desc";
                product.price = BigDecimal.valueOf(99.99);
                product.stock = 10;

                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body(product)
                                .when().post("/product")
                                .then()
                                .statusCode(201)
                                .body("name", is("Lifecycle Product"))
                                .extract().path("id");

                // Get
                given()
                                .when().get("/product/" + id)
                                .then()
                                .statusCode(200)
                                .body("name", is("Lifecycle Product"));

                // Update
                product.name = "Lifecycle Product Updated";
                given()
                                .contentType(ContentType.JSON)
                                .body(product)
                                .when().put("/product/" + id)
                                .then()
                                .statusCode(200)
                                .body("name", is("Lifecycle Product Updated"));

                // Delete
                given()
                                .when().delete("/product/" + id)
                                .then()
                                .statusCode(204);

                // Verify Deleted
                given()
                                .when().get("/product/" + id)
                                .then()
                                .statusCode(404);
        }

        @Test
        void testGetNotFound() {
                given().when().get("/product/99999").then().statusCode(404);
        }

        @Test
        void testUpdateNotFound() {
                Product product = new Product();
                product.name = "Test";
                given().contentType(ContentType.JSON).body(product).when().put("/product/99999").then().statusCode(404);
        }

        @Test
        void testUpdateInvalidName() {
                Product product = new Product();
                given().contentType(ContentType.JSON).body(product).when().put("/product/1").then().statusCode(422);
        }

        @Test
        void testDeleteNotFound() {
                given().when().delete("/product/99999").then().statusCode(404);
        }

        @Test
        void testCreateWithId_Fail() {
                Product product = new Product();
                product.id = 999L;
                product.name = "Invalid";

                given()
                                .contentType(ContentType.JSON)
                                .body(product)
                                .when().post("/product")
                                .then()
                                .statusCode(422);
        }
}
