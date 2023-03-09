package us.mytheria.bloblib.managers;

import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.inventory.MetaBlobInventory;
import us.mytheria.bloblib.entities.inventory.ReferenceMetaBlobInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaInventoryShard {
    private Map<String, ReferenceMetaBlobInventory> inventories;

    public MetaInventoryShard() {
        inventories = new HashMap<>();
    }

    protected void reload() {
        inventories = new HashMap<>();
    }

    protected void addInventory(MetaBlobInventory inventory, String key) {
        inventories.put(key, ReferenceMetaBlobInventory.of(inventory, key));
    }

    /**
     * Gets an inventory by its key.
     * If not found, will return null.
     *
     * @param key Key that points to the inventory
     * @return The inventory or null if not found
     */
    @Nullable
    public ReferenceMetaBlobInventory getInventory(String key) {
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
    public ReferenceMetaBlobInventory copyInventory(String key) {
        ReferenceMetaBlobInventory inventory = getInventory(key);
        if (inventory == null)
            return null;
        return inventory.copy();
    }

    /**
     * @return All inventories held by this shard.
     */
    public Collection<ReferenceMetaBlobInventory> allInventories() {
        return inventories.values();
    }

    /**
     * @return The size of the shard. Each inventory counts as 1.
     */
    public int size() {
        return inventories.size();
    }
}
