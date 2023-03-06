package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Holds the reference of the key that points to the inventory.
 * This key is tracked by entities such as BlobLib's InventoryManager.
 */
public class ReferenceMetaBlobInventory extends MetaBlobInventory {
    private final String key;

    public static ReferenceMetaBlobInventory of(MetaBlobInventory metaBlobInventory, String key) {
        return new ReferenceMetaBlobInventory(metaBlobInventory.getTitle(), metaBlobInventory.getSize(),
                metaBlobInventory.getButtonManager(), metaBlobInventory.getType(), key);
    }

    public ReferenceMetaBlobInventory(@NotNull String title, int size,
                                      @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                      @NotNull String type,
                                      @NotNull String key) {
        super(title, size, buttonManager, type);
        this.key = Objects.requireNonNull(key, "'key' cannot be null!");
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @Override
    @NotNull
    public ReferenceMetaBlobInventory copy() {
        return new ReferenceMetaBlobInventory(getTitle(), getSize(), getButtonManager(), getType(), getKey());
    }
}
