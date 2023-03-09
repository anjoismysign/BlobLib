package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BlobPlayerInventoryHolder extends PlayerInventoryBuilder<InventoryButton> {

    public static BlobPlayerInventoryHolder fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier, @Nullable UUID holderId) {
        return new BlobPlayerInventoryHolder(carrier.title(), carrier.size(), carrier.buttonManager(), holderId);
    }

    public BlobPlayerInventoryHolder(@NotNull String title, int size,
                                     @NotNull ButtonManager<InventoryButton> buttonManager,
                                     @Nullable UUID holderId) {
        super(title, size, buttonManager, holderId);
    }

    @Override
    @NotNull
    public BlobPlayerInventoryHolder copy() {
        return new BlobPlayerInventoryHolder(getTitle(), getSize(), getButtonManager(), getHolderId());
    }
}
