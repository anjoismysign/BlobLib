package us.mytheria.bloblib.entities.translatable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.events.TranslatableItemCloneEvent;

import java.util.List;
import java.util.Objects;

public interface TranslatableItem extends Translatable<ItemStack> {

    /**
     * Gets a TranslatableItem by its Material and ItemMeta
     *
     * @param itemStack The ItemStack to get the TranslatableItem by.
     * @param locale    The locale to get the TranslatableItem by.
     * @return The TranslatableItem, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableItem byItemStack(@NotNull ItemStack itemStack,
                                        @NotNull String locale) {
        Objects.requireNonNull(itemStack, "'itemStack' cannot be null");
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return null;
        List<TranslatableItem> translatableItems = BlobLibTranslatableAPI.getInstance()
                .getTranslatableItems(locale);
        translatableItems.forEach(translatableItem -> {
            ItemStack stack = translatableItem.get();
            ItemMeta stackMeta = stack.getItemMeta();
            if (stackMeta == null)
                return;
            if (stack.getType() != itemStack.getType())
                return;
            if (stackMeta.getCustomModelData() != meta.getCustomModelData())
                return;
        });
        return translatableItems.stream()
                .filter(translatableItem -> translatableItem.get().getType() == itemStack.getType())
                .filter(translatableItem -> {
                    ItemMeta stackMeta = translatableItem.get().getItemMeta();
                    if (stackMeta == null)
                        return false;
                    if (!stackMeta.hasCustomModelData())
                        return false;
                    return stackMeta.getCustomModelData() == meta.getCustomModelData();
                })
                .findFirst().orElse(null);
    }

    /**
     * Gets a TranslatableItem by its Material and ItemMeta in the default locale [en_us]
     *
     * @param itemStack The ItemStack to get the TranslatableItem by.
     * @return The TranslatableItem, or null if it doesn't exist.
     */
    static TranslatableItem byItemStack(@NotNull ItemStack itemStack) {
        return byItemStack(itemStack, "en_us");
    }

    /**
     * Gets a TranslatableItem by its Material and ItemMeta
     *
     * @param itemStack The ItemStack to get the TranslatableItem by.
     * @param player    The player to get the TranslatableItem's locale by.
     * @return The TranslatableItem, or null if it doesn't exist.
     */
    static TranslatableItem byItemStack(@NotNull ItemStack itemStack,
                                        @NotNull Player player) {
        return byItemStack(itemStack, player.getLocale());
    }

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

    /**
     * Checks whether an ItemStack is a TranslatableItem instance.
     *
     * @param item The ItemStack to check.
     * @return The TranslatableItem, or null if it isn't one.
     */
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

    /**
     * Localizes the TranslatableItem to a specific locale.
     *
     * @param locale The locale to localize to.
     * @return The localized TranslatableItem.
     */
    @Nullable
    default TranslatableItem localize(@NotNull String locale) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        if (getLocale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatableItem(getReference(),
                        locale);
    }

    /**
     * Localizes the TranslatableItem to a specific player's locale.
     *
     * @param player The player to localize to.
     * @return The localized TranslatableItem.
     */
    default TranslatableItem localize(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        return localize(player.getLocale());
    }

    /**
     * Will get the TranslatableItemModder for this TranslatableItem.
     *
     * @return The TranslatableItemModder.
     */
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

    /**
     * Applies the TranslatableItem to an existing ItemStack without overwriting its data.
     * This doesn't translate an already valid TranslatableItem instance!
     * It only applies the display name and lore.
     * It also sets the locale and reference in the ItemStack's NBT, making it a valid TranslatableItem instance.
     * This method is useful in cases of which another plugin manages ItemStacks.
     *
     * @param itemStack The ItemStack to apply the TranslatableItem to.
     * @param locale    The locale to apply.
     */
    default void apply(@NotNull ItemStack itemStack, @NotNull String locale) {
        Objects.requireNonNull(itemStack, "'hand' cannot be null");
        Objects.requireNonNull(locale, "'locale' cannot be null");
        TranslatableItem localized = localize(locale);
        locale = localized.getLocale();
        ItemStack translatedItem = localized.getClone();
        ItemMeta translatedMeta = translatedItem.getItemMeta();
        String name = translatedMeta.hasDisplayName() ? translatedMeta.getDisplayName() : null;
        List<String> lore = translatedMeta.hasLore() ? translatedMeta.getLore() : null;
        ItemMeta handMeta = itemStack.getItemMeta();
        if (name != null)
            handMeta.setDisplayName(name);
        if (lore != null)
            handMeta.setLore(lore);
        PersistentDataContainer container = handMeta.getPersistentDataContainer();
        if (!container.has(BlobTranslatableItem.keyKey, PersistentDataType.STRING))
            container.set(BlobTranslatableItem.keyKey, PersistentDataType.STRING, localized.getReference());
        if (!container.has(BlobTranslatableItem.localeKey, PersistentDataType.STRING))
            container.set(BlobTranslatableItem.localeKey, PersistentDataType.STRING, locale);
        itemStack.setItemMeta(handMeta);
    }

    /**
     * Applies the TranslatableItem to an existing ItemStack without overwriting its data.
     * This doesn't translate an already valid TranslatableItem instance!
     * It only applies the display name and lore.
     * It also sets the locale and reference in the ItemStack's NBT, making it a valid TranslatableItem instance.
     * This method is useful in cases of which another plugin manages ItemStacks.
     *
     * @param itemStack The ItemStack to apply the TranslatableItem to.
     * @param player    The player to apply the TranslatableItem to.
     */
    default void apply(@NotNull ItemStack itemStack, @NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        apply(itemStack, player.getLocale());
    }
}
