package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.entities.inventory.InventoryBuilderCarrier;
import io.github.anjoismysign.bloblib.entities.inventory.MetaBlobInventory;
import io.github.anjoismysign.bloblib.entities.inventory.MetaInventoryButton;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaInventoryShard {
    private Map<String, InventoryBuilderCarrier<MetaInventoryButton>> inventories;

    public MetaInventoryShard() {
        inventories = new HashMap<>();
    }

    protected void reload() {
        inventories = new HashMap<>();
    }

    protected void addInventory(InventoryBuilderCarrier<MetaInventoryButton> carrier, String key) {
        inventories.put(key, carrier);
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return inventories.get(key);
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
        InventoryBuilderCarrier<MetaInventoryButton> carrier = getMetaInventoryBuilderCarrier(key);
        if (carrier == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
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
     * @return All inventories held by this shard.
     */
    public List<MetaBlobInventory> allInventories() {
        return inventories.values().stream().map(MetaBlobInventory::fromInventoryBuilderCarrier).toList();
    }

    /**
     * @return The size of the shard. Each inventory counts as 1.
     */
    public int size() {
        return inventories.size();
    }
}
