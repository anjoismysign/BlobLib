package us.mytheria.bloblib.entities.translatable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;

import java.util.Objects;

public interface TranslatableItem extends Translatable<ItemStack> {
    /**
     * Gets a TranslatableItem by its key. Key is the same as getReference.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatableItem, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableItem by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatableItem(key);
    }

    @Nullable
    static TranslatableItem isInstance(@Nullable ItemStack item) {
        if (item == null)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(BlobTranslatableItem.keyKey, PersistentDataType.STRING))
            return null;
        if (!container.has(BlobTranslatableItem.localeKey, PersistentDataType.STRING))
            return null;
        String key = container.get(BlobTranslatableItem.keyKey, PersistentDataType.STRING);
        String locale = container.get(BlobTranslatableItem.localeKey, PersistentDataType.STRING);
        return BlobLibTranslatableAPI.getInstance().getTranslatableItem(key, locale);
    }

    @Nullable
    default TranslatableItem localize(String locale) {
        if (getLocale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatableItem(getReference(),
                        locale);
    }

    default TranslatableItemModder modder() {
        return TranslatableItemModder.mod(this);
    }

    default ItemStack getClone() {
        return new ItemStack(get());
    }
}
