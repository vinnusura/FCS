package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/fulfillment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FulfillmentResource {

    @Inject
    AssociateFulfillmentOperation associateFulfillmentOperation;

    @POST
    public Response create(CreateFulfillmentRequest request) {
        associateFulfillmentOperation.associate(request.storeId, request.productId, request.warehouseBuCode);
        return Response.ok().build();
    }
}
