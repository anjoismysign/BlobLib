package io.github.anjoismysign.bloblib.itemapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface DiscriminatorItemAPI extends ItemAPI {
    @Override
    default ItemAPIType getAPIType() {
        return ItemAPIType.DISCRIMINATOR;
    }

    /**
     * Sets a PersistentDataHolder to be of this ItemAPI.
     */
    default void set(@NotNull PersistentDataHolder holder) {
        Objects.requireNonNull(holder, "'holder' cannot be null");
        holder.getPersistentDataContainer().set(getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);
    }

    /**
     * @param itemStack The item you want to set through the ItemAPI
     * @return true if successful, false otherwise.
     */
    default boolean set(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        set(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return true;
    }

    /**
     * Sets all items in the ItemStack array through the ItemAPI.
     *
     * @param array The ItemStack array to use.
     */
    default void set(@NotNull ItemStack[] array) {
        Objects.requireNonNull(array, "'array' cannot be null");
        for (ItemStack itemStack : array) {
            set(itemStack);
        }
    }

    /**
     * Sets all items in the inventory through the ItemAPI.
     *
     * @param player The player to use.
     */
    default void set(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        set(player.getInventory().getContents());
        set(player.getInventory().getArmorContents());
    }

    default boolean isInstance(@NotNull PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(getNamespacedKey(), PersistentDataType.BYTE))
            return false;
        Byte b = container.get(getNamespacedKey(), PersistentDataType.BYTE);
        return b != null && b == 1;
    }
}
