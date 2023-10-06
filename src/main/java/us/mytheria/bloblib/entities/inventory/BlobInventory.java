package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlobInventory extends SharableInventory<InventoryButton> {

    public static BlobInventory fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier) {
        String title = Objects.requireNonNull(carrier.title(), "title cannot be null");
        return new BlobInventory(title, carrier.size(),
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
