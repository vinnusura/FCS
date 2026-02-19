package com.fulfilment.application.monolith.fulfillment;

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
    FulfillmentService service;

    @POST
    public Response create(CreateFulfillmentRequest request) {
        service.associate(request.storeId, request.productId, request.warehouseBuCode);
        return Response.ok().build();
    }
}
