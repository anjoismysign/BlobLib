package io.github.anjoismysign.bloblib.itemapi;

import io.github.anjoismysign.bloblib.BlobLibAssetAPI;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public interface ItemMaterialManager {

    static ItemMaterialManager getInstance() {
        return BlobLibAssetAPI.getInstance().getMaterialManager();
    }

    default void update(@Nullable ItemStack itemStack,
                        @Nullable Player player) {
        if (itemStack == null)
            return;
        @Nullable TranslatableItem translatableItem = TranslatableItem.byItemStack(itemStack);
        if (translatableItem == null)
            return;
        ItemStack copy = translatableItem.localize(player).getClone();
        itemStack.setItemMeta(copy.getItemMeta());
    }

    @Nullable
    default ItemMaterial of(@Nullable ItemStack itemStack) {
        @Nullable TranslatableItem translatableItem = TranslatableItem.byItemStack(itemStack);
        if (translatableItem == null)
            return null;
        return of(translatableItem);
    }

    @Nullable
    default ItemMaterial get(@NotNull String key) {
        @Nullable TranslatableItem lookup = BlobLibTranslatableAPI.getInstance().getTranslatableItem(key);
        return lookup == null ? null : of(lookup);
    }

    @NotNull
    default Map<String, ItemMaterial> getItems() {
        return BlobLibTranslatableAPI.getInstance().getTranslatableItems("en_us")
                .stream()
                .collect(Collectors.toMap(
                        DataAsset::identifier,
                        this::of
                ));
    }

    @NotNull
    private ItemMaterial of(@NotNull TranslatableItem translatableItem) {
        return new ItemMaterial() {
            @Override
            public @NotNull String getIdentifier() {
                return translatableItem.identifier();
            }

            @Override
            public @NotNull ItemStack toItemStack(@NotNull Player player) {
                return translatableItem.localize(player).getClone();
            }

            @Override
            public boolean isInstance(@NotNull ItemStack itemStack) {
                return TranslatableItem.byItemStack(itemStack) == null;
            }
        };
    }
}
