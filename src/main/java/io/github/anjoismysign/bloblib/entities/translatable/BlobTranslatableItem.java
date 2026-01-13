package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.entities.TranslatableRarity;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackModder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlobTranslatableItem implements TranslatableItem {

    @NotNull
    private final String locale, key, rarity;
    @NotNull
    private final Supplier<ItemStack> supplier;

    public static BlobTranslatableItem of(@NotNull String key,
                                          @NotNull String locale,
                                          @NotNull Supplier<ItemStack> supplier,
                                          @NotNull String rarity) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(supplier, "'supplier' cannot be null");
        return new BlobTranslatableItem(key, locale, supplier, rarity);
    }

    private BlobTranslatableItem(@NotNull String key,
                                 @NotNull String locale,
                                 @NotNull Supplier<ItemStack> supplier,
                                 @NotNull String rarity) {
        this.key = key;
        this.locale = locale;
        this.supplier = supplier;
        this.rarity = rarity;
    }

    @NotNull
    public String locale() {
        return locale;
    }

    @NotNull
    public ItemStack get() {
        return supplier.get();
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
        modder.itemName(function.apply(meta.getItemName()));
        modder.displayName(function.apply(meta.getDisplayName()));
        if (meta.hasLore())
            modder.lore(meta.getLore().stream()
                    .map(function)
                    .toList());
        return new BlobTranslatableItem(key, locale, ()->clone, rarity);
    }

    @Override
    public TranslatableRarity getRarity() {
        return BlobLibConfigManager.getInstance().getRarities().getRarity(rarity);
    }
}
