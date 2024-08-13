package us.mytheria.bloblib.entities;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;

public interface ReloadableUI {
    void reload(@NotNull BlobLibInventoryAPI inventoryAPI);
}
