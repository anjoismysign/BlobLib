package io.github.anjoismysign.bloblib.entities.inventory;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Holds the reference of the key that points to the inventory.
 * This key is tracked by entities such as BlobLib's InventoryManager.
 */
public class ReferenceMetaBlobInventory extends MetaBlobInventory {
    private final String key, locale;

    public static ReferenceMetaBlobInventory of(InventoryBuilderCarrier<MetaInventoryButton> carrier) {
        return new ReferenceMetaBlobInventory(carrier.title(), carrier.size(),
                carrier.buttonManager(), carrier.type(), carrier.reference(), carrier.locale());
    }

    public ReferenceMetaBlobInventory(@NotNull String title, int size,
                                      @NotNull ButtonManager<MetaInventoryButton> buttonManager,
                                      @NotNull String type,
                                      @NotNull String key,
                                      @NotNull String locale) {
        super(title, size, buttonManager, type);
        this.key = Objects.requireNonNull(key, "'key' cannot be null!");
        this.locale = Objects.requireNonNull(locale, "'locale' cannot be null!");
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public ReferenceMetaBlobInventory copy() {
        return new ReferenceMetaBlobInventory(getTitle(), getSize(),
                getButtonManager().copy(), getType(), getKey(), getLocale());
    }
}
