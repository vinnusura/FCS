package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class ProductRepositoryTest {

    @Inject
    ProductRepository productRepository;

    @Test
    void testProductPersistence() {
        Product product = new Product();
        product.name = "Test Product";
        product.description = "Test Description";
        product.price = BigDecimal.valueOf(10.50);
        product.stock = 50;

        productRepository.persist(product);

        assertNotNull(product.id);

        Product found = productRepository.findById(product.id);
        assertNotNull(found);
        assertEquals("Test Product", found.name);
        assertEquals(0, BigDecimal.valueOf(10.50).compareTo(found.price));
    }
}
