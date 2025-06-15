package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.api.BlobLibInventoryAPI;
import org.jetbrains.annotations.NotNull;

public interface ReloadableUI {
    void reload(@NotNull BlobLibInventoryAPI inventoryAPI);
}
