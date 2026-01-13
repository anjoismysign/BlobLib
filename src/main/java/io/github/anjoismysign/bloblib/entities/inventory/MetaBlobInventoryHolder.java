package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.anjo.entities.Result;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaBlobInventoryHolder extends InventoryHolderBuilder<MetaInventoryButton> {
    private final String type;

    public static MetaBlobInventoryHolder fromInventoryBuilderCarrier(InventoryBuilderCarrier<MetaInventoryButton> carrier, @NotNull InventoryHolder holder) {
        return new MetaBlobInventoryHolder(carrier.title(), carrier.size(), carrier.buttonManager(), carrier.type(), holder);
    }

    public MetaBlobInventoryHolder(@NotNull String title, int size,
                                   @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                   @Nullable String type, @NotNull InventoryHolder holder) {
        super(title, size, buttonManager, holder);
        this.type = type;
    }

    @Override
    @NotNull
    public MetaBlobInventoryHolder copy() {
        return new MetaBlobInventoryHolder(getTitle(), getSize(), getButtonManager(), getType(), getHolder());
    }

    /**
     * Will filter between buttons and will check if they have a valid Meta.
     * The first button from this filter to contain the provided slot will be returned
     * as a valid Result. An invalid Result will be returned if no candidate was found.
     *
     * @param slot The slot to check.
     * @return The button that has a valid Meta and contains a provided slot.
     */
    @NotNull
    public Result<MetaInventoryButton> belongsToAMetaButton(int slot) {
        MetaInventoryButton metaInventoryButton =
                getKeys().stream().map(this::getButton).filter(MetaInventoryButton::hasMeta)
                        .filter(button -> button.getSlots().contains(slot)).findFirst()
                        .orElse(null);
        return Result.ofNullable(metaInventoryButton);
    }

    @NotNull
    public String getType() {
        return type;
    }
}
