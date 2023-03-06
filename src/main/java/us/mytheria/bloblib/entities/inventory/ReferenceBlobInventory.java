package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

/**
 * Holds the reference of the key that points to the inventory.
 * This key is tracked by entities such as BlobLib's InventoryManager.
 */
public class ReferenceBlobInventory extends BlobInventory {
    private final String key;

    public ReferenceBlobInventory(@NotNull String title, int size,
                                  @NotNull ButtonManager<InventoryButton> buttonManager,
                                  @NotNull String key) {
        super(title, size, buttonManager);
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }
}
