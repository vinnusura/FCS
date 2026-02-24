package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateFulfillmentRequest {
    @NotNull(message = "Store ID cannot be null")
    public Long storeId;

    @NotNull(message = "Product ID cannot be null")
    public Long productId;

    @NotBlank(message = "Warehouse BU Code cannot be blank")
    public String warehouseBuCode;
}
