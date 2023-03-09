package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MetaBlobPlayerInventoryHolder extends PlayerInventoryBuilder<MetaInventoryButton> {

    public static MetaBlobPlayerInventoryHolder fromInventoryBuilderCarrier(InventoryBuilderCarrier<MetaInventoryButton> carrier, @Nullable UUID holderId) {
        return new MetaBlobPlayerInventoryHolder(carrier.title(), carrier.size(), carrier.buttonManager(), holderId);
    }

    public MetaBlobPlayerInventoryHolder(@NotNull String title, int size,
                                         @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                         @Nullable UUID holderId) {
        super(title, size, buttonManager, holderId);
    }

    @Override
    @NotNull
    public MetaBlobPlayerInventoryHolder copy() {
        return new MetaBlobPlayerInventoryHolder(getTitle(), getSize(), getButtonManager(), getHolderId());
    }
}
