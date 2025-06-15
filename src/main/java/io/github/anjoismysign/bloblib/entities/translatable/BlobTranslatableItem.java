package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.itemstack.ItemStackModder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableItem implements TranslatableItem {
    static final NamespacedKey keyKey = new NamespacedKey(BlobLib.getInstance(), "item_key");
    static final NamespacedKey localeKey = new NamespacedKey(BlobLib.getInstance(), "item_locale");

    @NotNull
    private final String locale, key;
    @NotNull
    private final ItemStack itemStack;

    public static BlobTranslatableItem of(@NotNull String key,
                                          @NotNull String locale,
                                          @NotNull ItemStack itemStack) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(itemStack, "'itemStack' cannot be null");
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
    public String locale() {
        return locale;
    }

    @NotNull
    public ItemStack get() {
        return itemStack;
    }

    @NotNull
    public String identifier() {
        return key;
    }

    @Override
    @NotNull
    public TranslatableItem modify(Function<String, String> function) {
        ItemStack clone = getClone(false);
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
