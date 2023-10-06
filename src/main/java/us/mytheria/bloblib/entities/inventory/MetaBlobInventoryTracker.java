package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;

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
                string -> BlobLibInventoryAPI.getInstance()
                        .buildMetaInventory(registry.getKey(), string));
    }
}
