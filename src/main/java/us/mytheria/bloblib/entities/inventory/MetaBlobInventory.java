package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MetaBlobInventory extends SharableInventory<MetaInventoryButton> {
    private final String type;

    public static MetaBlobInventory fromInventoryBuilderCarrier(InventoryBuilderCarrier<MetaInventoryButton> carrier) {
        return new MetaBlobInventory(carrier.title(), carrier.size(),
                carrier.buttonManager().copy(), carrier.type());
    }

    public MetaBlobInventory(@NotNull String title, int size,
                             @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                             @NotNull String type) {
        super(title, size, buttonManager);
        this.type = Objects.requireNonNull(type, "'type' cannot be null!");
    }

    @Override
    @NotNull
    public MetaBlobInventory copy() {
        return new MetaBlobInventory(getTitle(), getSize(),
                getButtonManager().copy(), getType());
    }

    /**
     * Will filter between buttons and will check if they have a valid Meta.
     * First button from this filter to contain the provided slot will be returned
     * as a valid Result. An invalid Result will be returned if no candidate was found.
     *
     * @param slot The slot to check.
     * @return The button that has a valid Meta and contains provided slot.
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
