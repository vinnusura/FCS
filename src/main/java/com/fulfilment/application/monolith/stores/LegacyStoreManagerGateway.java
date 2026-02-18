package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    // Emulates sending data to a legacy system by writing to a temp file
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    // Emulates sending data to a legacy system by writing to a temp file
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      // Step 1: Create a temporary file
      Path tempFile = Files.createTempFile(store.name, ".txt");
      LOGGER.infof("Temporary file created at: %s", tempFile);

      // Step 2: Write data to the temporary file
      String content = String.format("Store created. [ name = %s ] [ items on stock = %d ]",
          store.name, store.quantityProductsInStock);
      Files.write(tempFile, content.getBytes());
      LOGGER.info("Data written to temporary file.");

      // Step 3: Delete the temporary file when done
      Files.delete(tempFile);
      LOGGER.info("Temporary file deleted.");

    } catch (Exception e) {
      LOGGER.error("Failed to write to legacy system emulation file", e);
    }
  }
}
