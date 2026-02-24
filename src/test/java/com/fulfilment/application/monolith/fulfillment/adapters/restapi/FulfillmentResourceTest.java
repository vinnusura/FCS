package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FulfillmentResourceTest {

    @InjectMock
    AssociateFulfillmentOperation associateFulfillmentOperation;

    @Test
    void testCreateFulfillment_Success() {
        CreateFulfillmentRequest request = new CreateFulfillmentRequest();
        request.storeId = 1L;
        request.productId = 2L;
        request.warehouseBuCode = "WH-001";

        doNothing().when(associateFulfillmentOperation).associate(1L, 2L, "WH-001");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/fulfillment")
                .then()
                .statusCode(200);

        verify(associateFulfillmentOperation).associate(1L, 2L, "WH-001");
    }

    @Test
    void testCreateFulfillment_BadRequest_ServiceError() {
        CreateFulfillmentRequest request = new CreateFulfillmentRequest();
        request.storeId = 1L;
        request.productId = 2L;
        request.warehouseBuCode = "WH-001";

        doThrow(new IllegalArgumentException("Invalid")).when(associateFulfillmentOperation).associate(1L, 2L,
                "WH-001");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/fulfillment")
                .then()
                .statusCode(500); // Quarkus converts uncaught IllegalArgumentException to 500 by default unless
                                  // mapped
    }
}
