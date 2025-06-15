package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.api.BlobLibInventoryAPI;
import org.jetbrains.annotations.NotNull;

public class MetaBlobInventoryTracker extends InventoryTracker<MetaBlobInventory, MetaInventoryButton> {
    public static MetaBlobInventoryTracker of(
            @NotNull MetaBlobInventory metaBlobInventory,
            @NotNull String locale,
            @NotNull InventoryDataRegistry<MetaInventoryButton> registry) {
        return new MetaBlobInventoryTracker(metaBlobInventory, locale, registry);
    }

    protected MetaBlobInventoryTracker(MetaBlobInventory metaBlobInventory,
                                       String locale,
                                       InventoryDataRegistry<MetaInventoryButton> registry) {
        super(metaBlobInventory, locale, registry,
                string -> BlobLibInventoryAPI.getInstance().getInventoryManager()
                        .cloneMetaInventory(registry.getKey(), string));
    }
}
