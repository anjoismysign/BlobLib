package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

public class BlobInventory extends SharableInventory<InventoryButton> {

    public static BlobInventory fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier) {
        return new BlobInventory(carrier.title(), carrier.size(),
                carrier.buttonManager().copy());
    }

    public BlobInventory(@NotNull String title, int size,
                         @NotNull ButtonManager<InventoryButton> buttonManager) {
        super(title, size, buttonManager);
    }

    @Override
    @NotNull
    public BlobInventory copy() {
        return new BlobInventory(getTitle(), getSize(), getButtonManager().copy());
    }
}
