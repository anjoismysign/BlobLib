package us.mytheria.bloblib.managers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.inventory.MetaBlobInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetaInventoryShard {
    private Map<String, MetaBlobInventory> inventories;

    public MetaInventoryShard() {
        inventories = new HashMap<>();
    }

    protected void reload() {
        inventories = new HashMap<>();
    }

    protected void addInventory(MetaBlobInventory inventory) {
        inventories.put(inventory.getType(), inventory);
    }

    /**
     * Gets an inventory by its key.
     * If not found, will return null.
     *
     * @param key Key that points to the inventory
     * @return The inventory or null if not found
     */
    @Nullable
    public MetaBlobInventory getInventory(String key) {
        return inventories.get(key);
    }

    /**
     * Gets an inventory by its key and makes a copy of it.
     * If not found, will return null.
     *
     * @param key Key that points to the inventory
     * @return The inventory or null if not found
     */
    @Nullable
    public MetaBlobInventory copyInventory(String key) {
        MetaBlobInventory inventory = getInventory(key);
        if (inventory == null)
            return null;
        return inventory.copy();
    }

    /**
     * @return Set view of the map's entries
     */
    @NotNull
    public Set<Map.Entry<String, MetaBlobInventory>> entrySet() {
        return inventories.entrySet();
    }
}
