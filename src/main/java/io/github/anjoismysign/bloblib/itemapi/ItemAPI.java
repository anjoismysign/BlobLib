package io.github.anjoismysign.bloblib.itemapi;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface ItemAPI {
    NamespacedKey getNamespacedKey();

    ItemAPIType getAPIType();

    /**
     * @param holder The PersistentDataHolder you want to check.
     * @return true if the PersistentDataHolder is instance, false otherwise
     */
    boolean isInstance(@NotNull PersistentDataHolder holder);

    /**
     * @param itemStack The item you want to check.
     * @return true if the item is instance, false otherwise.
     */
    default boolean isInstance(
            @Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return isInstance(itemMeta);
    }

    /**
     * Will drop all item instances from the inventory.
     *
     * @param inventoryHolder The inventory holder you want to drop the items from.
     * @param dropLocation    The location where the items will be dropped.
     */
    default void dropAll(
            @NotNull InventoryHolder inventoryHolder,
            @NotNull Location dropLocation) {
        Objects.requireNonNull(inventoryHolder, "'inventoryHolder' cannot be null");
        Objects.requireNonNull(dropLocation, "'dropLocation' cannot be null");
        Objects.requireNonNull(dropLocation.getWorld(), "'dropLocation' has no World set");
        for (ItemStack itemStack : inventoryHolder.getInventory().getContents()) {
            if (itemStack == null)
                continue;
            if (isInstance(itemStack)) continue;
            dropLocation.getWorld().dropItemNaturally(dropLocation, itemStack.clone());
            itemStack.setAmount(0);
        }
    }

    /**
     * Will drop all item instances from the inventory.
     *
     * @param player The player you want to drop the items from.
     */
    default void dropAll(@NotNull Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");
        dropAll(player, player.getLocation());
    }

    /**
     * @param inventory The inventory you want to check.
     * @return A list of item instances in the Inventory.
     */
    default List<ItemStack> getInstances(@NotNull Inventory inventory) {
        Objects.requireNonNull(inventory, "'inventory' cannot be null");
        List<ItemStack> instances = new ArrayList<>();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null)
                continue;
            if (isInstance(itemStack)) instances.add(itemStack);
        }
        return instances;
    }

    /**
     * @param inventoryHolder The inventory holder you want to check.
     * @return A list of item instances from the InventoryHolder.
     */
    default List<ItemStack> getInstances(@NotNull InventoryHolder inventoryHolder) {
        Objects.requireNonNull(inventoryHolder, "'inventoryHolder' cannot be null");
        return getInstances(inventoryHolder.getInventory());
    }

    /**
     * Checks whether an Entity is instance or if it has instances in its inventory
     *
     * @param entity The entity to check
     * @return True if the entity is instance or if its inventory has instances, false otherwise
     */
    default boolean isOrHoldsInstance(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "'entity' cannot be null");
        if (isInstance(entity))
            return true;
        if (entity instanceof InventoryHolder inventoryHolder)
            return !getInstances(inventoryHolder).isEmpty();
        return false;
    }
}
