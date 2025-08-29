package io.github.anjoismysign.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlobPlayerInventoryHolder extends PlayerInventoryBuilder<InventoryButton> {

    public static BlobPlayerInventoryHolder fromInventoryBuilderCarrier(InventoryBuilderCarrier<InventoryButton> carrier, @NotNull UUID holderId) {
        return new BlobPlayerInventoryHolder(carrier.title(), carrier.size(), carrier.buttonManager(), holderId);
    }

    public BlobPlayerInventoryHolder(@NotNull String title, int size,
                                     @NotNull ButtonManager<InventoryButton> buttonManager,
                                     @NotNull UUID holderId) {
        super(title, size, buttonManager, holderId);
    }

    @Override
    @NotNull
    public BlobPlayerInventoryHolder copy() {
        return new BlobPlayerInventoryHolder(getTitle(), getSize(), getButtonManager(), getPlayerUniqueId());
    }

}
