package us.mytheria.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MetaBlobPlayerInventoryBuilder extends PlayerInventoryBuilder<MetaInventoryButton> {

    public static MetaBlobPlayerInventoryBuilder fromInventoryBuilderCarrier(InventoryBuilderCarrier<MetaInventoryButton> carrier, @Nullable UUID holderId) {
        return new MetaBlobPlayerInventoryBuilder(carrier.title(), carrier.size(), carrier.buttonManager(), holderId);
    }

    public MetaBlobPlayerInventoryBuilder(@NotNull String title, int size,
                                          @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                          @Nullable UUID holderId) {
        super(title, size, buttonManager, holderId);
    }

    @Override
    @NotNull
    public MetaBlobPlayerInventoryBuilder copy() {
        return new MetaBlobPlayerInventoryBuilder(getTitle(), getSize(), getButtonManager(), getHolderId());
    }

    @Override
    @NotNull
    public MetaBlobPlayerInventoryBuilder setHolderId(@NotNull UUID holderId) {
        return new MetaBlobPlayerInventoryBuilder(getTitle(), getSize(), getButtonManager(), holderId);
    }
}