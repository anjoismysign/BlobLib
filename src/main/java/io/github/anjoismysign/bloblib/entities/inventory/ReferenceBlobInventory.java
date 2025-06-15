package io.github.anjoismysign.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

/**
 * Holds the reference of the key that points to the inventory.
 * This key is tracked by entities such as BlobLib's InventoryManager.
 */
public class ReferenceBlobInventory extends BlobInventory {
    private final String key;

    public static ReferenceBlobInventory of(InventoryBuilderCarrier<InventoryButton> carrier) {
        return new ReferenceBlobInventory(carrier.title(), carrier.size(),
                carrier.buttonManager(), carrier.reference());
    }

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

    @Override
    @NotNull
    public ReferenceBlobInventory copy() {
        return new ReferenceBlobInventory(getTitle(), getSize(), getButtonManager(), getKey());
    }
}
