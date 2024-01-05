package us.mytheria.bloblib.entities.translatable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.events.TranslatableItemCloneEvent;

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

    /**
     * Will attempt to localize a specific ItemStack while preserving
     * their NBTs.
     *
     * @param itemStack The ItemStack to process. If null, nothing happens.
     * @param locale    The target locale.
     */
    static void localize(@Nullable ItemStack itemStack,
                         @NotNull String locale) {
        TranslatableItem translatableItem = TranslatableItem.isInstance(itemStack);
        if (translatableItem == null)
            return;
        TranslatableItem localized = translatableItem.localize(locale);
        ItemStack toStack = localized.getClone();
        ItemMeta from = itemStack.getItemMeta();
        Objects.requireNonNull(from);
        ItemMeta to = toStack.getItemMeta();
        Objects.requireNonNull(to);
        if (to.hasDisplayName())
            from.setDisplayName(to.getDisplayName());
        else
            from.setDisplayName(null);
        if (to.hasLore())
            from.setLore(to.getLore());
        else
            from.setLore(null);
        PersistentDataContainer container = from.getPersistentDataContainer();
        container.set(BlobTranslatableItem.localeKey, PersistentDataType.STRING, localized.getLocale());
        itemStack.setItemMeta(from);
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

    /**
     * Will get a clone of the TranslatableItem, allowing
     * other plugins to modify the ItemStack.
     *
     * @param callEvent If the event should be called.
     * @return The clone.
     */
    default ItemStack getClone(boolean callEvent) {
        ItemStack clone = new ItemStack(get());
        if (!callEvent)
            return clone;
        TranslatableItemCloneEvent event = new TranslatableItemCloneEvent(this, clone);
        Bukkit.getPluginManager().callEvent(event);
        return event.getClone();
    }

    /**
     * Will get a clone of the TranslatableItem, allowing
     * other plugins to modify the ItemStack.
     * Event will be called.
     *
     * @return The clone.
     */
    default ItemStack getClone() {
        return getClone(true);
    }
}
