package us.mytheria.bloblib.entities.translatable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.events.TranslatableItemCloneEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface TranslatableItem extends Translatable<ItemStack> {

    static final String KEY_PREFIX = "translatableitem_key:";
    static final String LOCALE_PREFIX = "translatableitem_locale:";

    /**
     * Gets a TranslatableItem by an ItemStack
     *
     * @param itemStack The ItemStack to get the TranslatableItem by.
     * @return The TranslatableItem, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableItem byItemStack(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "'itemStack' cannot be null");
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;
        CustomModelDataComponent dataComponent = itemMeta.getCustomModelDataComponent();
        String key = null;
        String locale = null;
        for (String data : dataComponent.getStrings()) {
            if (data.startsWith(KEY_PREFIX))
                key = data.replace(KEY_PREFIX, "");
            if (data.startsWith(LOCALE_PREFIX))
                locale = data.replace(LOCALE_PREFIX, "");
        }
        if (key == null || locale == null)
            return null;
        @Nullable TranslatableItem translatableItem = TranslatableItem.by(key);
        if (translatableItem == null)
            return null;
        return translatableItem.localize(locale);
    }

    /**
     * Gets a TranslatableItem by its key. Key is the same as identifier.
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
        TranslatableItem translatableItem = TranslatableItem.byItemStack(itemStack);
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
        var component = from.getCustomModelDataComponent();
        List<String> get = component.getStrings();
        List<String> list = new ArrayList<>(get
                .stream()
                .filter(line -> !line.startsWith(LOCALE_PREFIX)).toList());
        list.add(LOCALE_PREFIX + locale);
        component.setStrings(list);
        from.setCustomModelDataComponent(component);
        itemStack.setItemMeta(from);
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
        if (locale().equals(locale))
            return this;
        return BlobLibTranslatableAPI.getInstance()
                .getTranslatableItem(identifier(),
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
        locale = localized.locale();
        String key = localized.identifier();
        ItemStack translatedItem = localized.getClone();
        ItemMeta translatedMeta = translatedItem.getItemMeta();
        String name = translatedMeta.hasDisplayName() ? translatedMeta.getDisplayName() : null;
        List<String> lore = translatedMeta.hasLore() ? translatedMeta.getLore() : null;
        ItemMeta handMeta = itemStack.getItemMeta();
        if (name != null)
            handMeta.setDisplayName(name);
        if (lore != null)
            handMeta.setLore(lore);
        var component = handMeta.getCustomModelDataComponent();
        List<String> get = component.getStrings();
        List<String> list = new ArrayList<>(get
                .stream()
                .filter(line -> !line.startsWith(LOCALE_PREFIX))
                .filter(line -> !line.startsWith(KEY_PREFIX)).toList());
        list.add(KEY_PREFIX + key);
        list.add(LOCALE_PREFIX + locale);
        component.setStrings(list);
        handMeta.setCustomModelDataComponent(component);
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
