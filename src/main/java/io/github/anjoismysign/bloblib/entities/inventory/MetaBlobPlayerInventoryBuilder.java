package io.github.anjoismysign.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MetaBlobPlayerInventoryBuilder extends PlayerInventoryBuilder<MetaInventoryButton> {

    public static MetaBlobPlayerInventoryBuilder fromInventoryBuilderCarrier(InventoryBuilderCarrier<MetaInventoryButton> carrier, @NotNull UUID holderId) {
        return new MetaBlobPlayerInventoryBuilder(carrier.title(), carrier.size(), carrier.buttonManager(), holderId);
    }

    public MetaBlobPlayerInventoryBuilder(@NotNull String title, int size,
                                          @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                          @NotNull UUID holderId) {
        super(title, size, buttonManager, holderId);
    }

    @Override
    @NotNull
    public MetaBlobPlayerInventoryBuilder copy() {
        return new MetaBlobPlayerInventoryBuilder(getTitle(), getSize(), getButtonManager(), getPlayerUniqueId());
    }

}