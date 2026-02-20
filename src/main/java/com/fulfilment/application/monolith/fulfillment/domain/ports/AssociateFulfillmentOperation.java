package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface AssociateFulfillmentOperation {
    void associate(Long storeId, Long productId, String warehouseBuCode);
}
