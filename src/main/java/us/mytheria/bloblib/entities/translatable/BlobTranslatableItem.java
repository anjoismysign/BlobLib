package us.mytheria.bloblib.entities.translatable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.itemstack.ItemStackModder;

import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableItem implements TranslatableItem {
    protected static final NamespacedKey keyKey = new NamespacedKey(BlobLib.getInstance(), "item_key");
    protected static final NamespacedKey localeKey = new NamespacedKey(BlobLib.getInstance(), "item_locale");

    @NotNull
    private final String locale, key;
    @NotNull
    private final ItemStack itemStack;

    public static BlobTranslatableItem of(@NotNull String key,
                                          @NotNull String locale,
                                          @NotNull ItemStack itemStack) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta);
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(keyKey, PersistentDataType.STRING))
            container.set(keyKey, PersistentDataType.STRING, key);
        if (!container.has(localeKey, PersistentDataType.STRING))
            container.set(localeKey, PersistentDataType.STRING, locale);
        itemStack.setItemMeta(itemMeta);
        return new BlobTranslatableItem(key, locale, itemStack);
    }

    private BlobTranslatableItem(@NotNull String key,
                                 @NotNull String locale,
                                 @NotNull ItemStack itemStack) {
        this.key = key;
        this.locale = locale;
        this.itemStack = itemStack;
    }

    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public ItemStack get() {
        return itemStack;
    }

    @NotNull
    public String getReference() {
        return key;
    }

    public DataAssetType getType() {
        return DataAssetType.TRANSLATABLE_ITEM;
    }

    @Override
    @NotNull
    public TranslatableItem modify(Function<String, String> function) {
        ItemStack clone = getClone();
        ItemMeta meta = clone.getItemMeta();
        Objects.requireNonNull(meta, "ItemMeta cannot be null");
        ItemStackModder modder = ItemStackModder.mod(clone);
        modder.displayName(function.apply(meta.getDisplayName()));
        if (meta.hasLore())
            modder.lore(meta.getLore().stream()
                    .map(function)
                    .toList());
        return new BlobTranslatableItem(key, locale, clone);
    }
}
