package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;

/**
 * A Tracker for {@link BlobInventory}.
 * An InventoryTracker is used to track the inventory of a player
 * in cases such as changing locale, or changing the inventory.
 */
public class BlobInventoryTracker extends InventoryTracker<BlobInventory, InventoryButton> {
    public static BlobInventoryTracker of(
            @NotNull BlobInventory blobInventory,
            @NotNull String locale,
            @NotNull InventoryDataRegistry<InventoryButton> registry) {
        return new BlobInventoryTracker(blobInventory, locale, registry);
    }

    protected BlobInventoryTracker(BlobInventory blobInventory,
                                   String locale,
                                   InventoryDataRegistry<InventoryButton> registry) {
        super(blobInventory, locale, registry,
                string -> BlobLibInventoryAPI.getInstance()
                        .buildInventory(registry.getKey(), string));
    }
}
