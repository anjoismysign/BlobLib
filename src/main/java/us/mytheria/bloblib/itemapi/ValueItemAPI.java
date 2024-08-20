package us.mytheria.bloblib.itemapi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface ValueItemAPI<T> extends ItemAPI {

    default boolean isSameClass(@NotNull Object value) {
        Class<?> clazz = getAPIType().getPersistentDataType().getComplexType();
        return clazz.equals(value.getClass());
    }

    @NotNull
    default T get(@NotNull PersistentDataHolder holder) {
        Objects.requireNonNull(holder, "'holder' cannot be null");
        Object read = getAPIType().get(holder.getPersistentDataContainer(), getNamespacedKey());
        if (!isSameClass(read))
            throw new IllegalArgumentException("Wrong getItemAPIType return. Is generic not a primitive wrapper?");
        @SuppressWarnings("unchecked")
        T value = (T) read;
        return value;
    }

    /**
     * Sets a PersistentDataHolder to be of this ItemAPI.
     */
    default void set(@NotNull PersistentDataHolder holder,
                     @NotNull T value) {
        Objects.requireNonNull(holder, "'holder' cannot be null");
        Objects.requireNonNull(value, "'value' cannot be null");
        getAPIType().set(holder.getPersistentDataContainer(), getNamespacedKey(), value);
    }

    /**
     * @param itemStack The item you want to set through the ItemAPI
     * @return true if successful, false otherwise.
     */
    default boolean set(
            @Nullable ItemStack itemStack,
            @NotNull T value
    ) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        set(itemMeta, value);
        itemStack.setItemMeta(itemMeta);
        return true;
    }

    /**
     * Sets all items in the ItemStack array through the ItemAPI.
     *
     * @param array The ItemStack array to use.
     */
    default void set(@NotNull ItemStack[] array,
                     @NotNull T value) {
        Objects.requireNonNull(array, "'array' cannot be null");
        for (ItemStack itemStack : array) {
            set(itemStack, value);
        }
    }

    /**
     * Sets all items in the inventory through the ItemAPI.
     *
     * @param player The player to use.
     */
    default void set(@NotNull Player player,
                     @NotNull T value) {
        Objects.requireNonNull(player, "'player' cannot be null");
        set(player.getInventory().getContents(), value);
        set(player.getInventory().getArmorContents(), value);
    }

    default boolean isInstance(@NotNull PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        return getAPIType().has(container, getNamespacedKey());
    }

    /**
     * Gets all the instances values from an Entity by checking its PersistentDataContainer and its inventory.
     *
     * @param entity The entity to check
     * @return A list holding all the values
     */
    @NotNull
    default List<T> getAll(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "'entity' cannot be null");
        List<T> values = new ArrayList<>();
        if (isInstance(entity))
            values.add(get(entity));
        if (entity instanceof InventoryHolder inventoryHolder) {
            for (ItemStack itemStack : inventoryHolder.getInventory().getContents()) {
                if (itemStack == null)
                    continue;
                if (isInstance(itemStack)) {
                    T t = get(itemStack.getItemMeta());
                    values.add(t);
                }
            }
        }
        return values;
    }
}
