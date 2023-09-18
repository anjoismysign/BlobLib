package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlobInventoryHolder extends InventoryHolderBuilder<InventoryButton> {

    public static BlobInventoryHolder fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier,
                                                                  @Nullable InventoryHolder holder) {
        return new BlobInventoryHolder(carrier.title(), carrier.size(), carrier.buttonManager(), holder);
    }

    public BlobInventoryHolder(@NotNull String title, int size,
                               @NotNull ButtonManager<InventoryButton> buttonManager,
                               @Nullable InventoryHolder holder) {
        super(title, size, buttonManager, holder);
    }

    @Override
    @NotNull
    public BlobInventoryHolder copy() {
        return new BlobInventoryHolder(getTitle(), getSize(), getButtonManager(), getHolder());
    }

    @Override
    @NotNull
    public BlobInventoryHolder setHolder(@NotNull InventoryHolder holder) {
        return new BlobInventoryHolder(getTitle(), getSize(), getButtonManager(), holder);
    }
}
