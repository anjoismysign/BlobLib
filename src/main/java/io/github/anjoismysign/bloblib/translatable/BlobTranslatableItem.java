package io.github.anjoismysign.bloblib.translatable;

import io.github.anjoismysign.bloblib.FluidPressureAPI;
import io.github.anjoismysign.bloblib.ProjectileDamageAPI;
import io.github.anjoismysign.bloblib.SoulAPI;
import io.github.anjoismysign.bloblib.UniqueAPI;
import io.github.anjoismysign.bloblib.domain.TranslatableRarity;
import io.github.anjoismysign.bloblib.manager.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.middleman.itemstack.ItemStackModder;
import io.github.anjoismysign.bloblib.middleman.itemstack.OmniStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlobTranslatableItem implements TranslatableItem {

    @NotNull
    private final String locale, key, rarity;
    @NotNull
    private final Supplier<ItemStack> supplier;

    /**
     * The non-translatable blueprint this item was built from, or null if this
     * instance was derived from an already built ItemStack (see {@link #modify(Function)}).
     * A locale overlay needs it to inherit everything but the translatable text.
     */
    @Nullable
    private final OmniStack omniStack;

    private final boolean isSoul, isUnique;

    @Nullable
    private final Double fluidPressure, projectileDamage;

    /**
     * Builds a TranslatableItem out of its blueprint. Everything but the
     * translatable text held by the OmniStack is non-translatable, and is what a
     * locale overlay of this item inherits.
     *
     * @param key              The identifier of the item
     * @param locale           The locale of the item
     * @param omniStack        The blueprint the ItemStack is built from
     * @param rarity           The name of the rarity
     * @param isSoul           Whether the item is soulbound
     * @param isUnique         Whether the item is unique
     * @param fluidPressure    The fluid pressure, or null if it has none
     * @param projectileDamage The projectile damage, or null if it has none
     * @return The TranslatableItem
     */
    @NotNull
    public static BlobTranslatableItem of(@NotNull String key,
                                          @NotNull String locale,
                                          @NotNull OmniStack omniStack,
                                          @NotNull String rarity,
                                          boolean isSoul,
                                          boolean isUnique,
                                          @Nullable Double fluidPressure,
                                          @Nullable Double projectileDamage) {
        Objects.requireNonNull(key, "'key' cannot be null");
        Objects.requireNonNull(locale, "'locale' cannot be null");
        Objects.requireNonNull(omniStack, "'omniStack' cannot be null");
        return new BlobTranslatableItem(key, locale, omniStack, rarity,
                isSoul, isUnique, fluidPressure, projectileDamage);
    }

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
                                 @NotNull OmniStack omniStack,
                                 @NotNull String rarity,
                                 boolean isSoul,
                                 boolean isUnique,
                                 @Nullable Double fluidPressure,
                                 @Nullable Double projectileDamage) {
        this.key = key;
        this.locale = locale;
        this.rarity = rarity;
        this.omniStack = omniStack;
        this.isSoul = isSoul;
        this.isUnique = isUnique;
        this.fluidPressure = fluidPressure;
        this.projectileDamage = projectileDamage;
        this.supplier = () -> {
            ItemStack itemStack = omniStack.getCopy();
            @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta, "'itemMeta' cannot be null");
            CustomModelDataComponent dataComponent = itemMeta.getCustomModelDataComponent();
            List<String> list = new ArrayList<>(dataComponent.getStrings());
            list.add(TranslatableItem.KEY_PREFIX + key);
            list.add(TranslatableItem.LOCALE_PREFIX + locale);
            dataComponent.setStrings(list);
            itemMeta.setCustomModelDataComponent(dataComponent);
            itemStack.setItemMeta(itemMeta);
            if (isSoul)
                SoulAPI.getInstance().set(itemStack);
            if (isUnique)
                UniqueAPI.getInstance().set(itemStack);
            if (fluidPressure != null)
                FluidPressureAPI.getInstance().set(itemStack, fluidPressure);
            if (projectileDamage != null)
                ProjectileDamageAPI.getInstance().set(itemStack, projectileDamage);
            return itemStack;
        };
    }

    private BlobTranslatableItem(@NotNull String key,
                                 @NotNull String locale,
                                 @NotNull Supplier<ItemStack> supplier,
                                 @NotNull String rarity) {
        this.key = key;
        this.locale = locale;
        this.supplier = supplier;
        this.rarity = rarity;
        this.omniStack = null;
        this.isSoul = false;
        this.isUnique = false;
        this.fluidPressure = null;
        this.projectileDamage = null;
    }

    /**
     * Derives a locale overlay of this item.
     * <p>
     * Every non-translatable field is inherited from this item. The translatable
     * text is not: it starts unset, as if the ItemMeta had never been touched,
     * and only the fields the overlay provides are applied. An overlay that
     * declares an ItemName alone therefore shows the vanilla display name and no
     * lore.
     *
     * @param locale      The locale of the overlay
     * @param displayName The display name, or null to leave it unset
     * @param itemName    The item name, or null to leave it unset
     * @param lore        The lore, or null to leave it unset
     * @param miniMessage Whether the overlay parses as MiniMessage, or null to inherit
     * @return The overlaying TranslatableItem
     */
    @NotNull
    public BlobTranslatableItem overlay(@NotNull String locale,
                                        @Nullable String displayName,
                                        @Nullable String itemName,
                                        @Nullable List<String> lore,
                                        @Nullable Boolean miniMessage) {
        Objects.requireNonNull(locale, "'locale' cannot be null");
        OmniStack base = Objects.requireNonNull(omniStack,
                "'" + key + "' TranslatableItem cannot be overlaid because it was not read from a configuration");
        OmniStack derived = new OmniStack(base.stackSupplier(),
                base.builderConsumer(),
                base.linkedItem(),
                miniMessage == null ? base.isMiniMessage() : miniMessage,
                lore,
                displayName,
                itemName);
        return of(key, locale, derived, rarity, isSoul, isUnique, fluidPressure, projectileDamage);
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
        if (meta.hasItemName()) {
            modder.itemName(function.apply(meta.getItemName()));
        }
        if (meta.hasDisplayName()) {
            modder.displayName(function.apply(meta.getDisplayName()));
        }
        if (meta.hasLore()) {
            modder.lore(meta.getLore().stream()
                    .map(function)
                    .toList());
        }
        return new BlobTranslatableItem(key, locale, () -> clone, rarity);
    }

    @Override
    public TranslatableRarity getRarity() {
        return BlobLibConfigManager.getInstance().getRarities().getRarity(rarity);
    }
}
