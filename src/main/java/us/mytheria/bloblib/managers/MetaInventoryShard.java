package us.mytheria.bloblib.managers;

import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.inventory.InventoryBuilderCarrier;
import us.mytheria.bloblib.entities.inventory.MetaInventoryButton;
import us.mytheria.bloblib.entities.inventory.ReferenceMetaBlobInventory;

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
    public ReferenceMetaBlobInventory getInventory(String key) {
        InventoryBuilderCarrier<MetaInventoryButton> carrier = getMetaInventoryBuilderCarrier(key);
        if (carrier == null)
            return null;
        return ReferenceMetaBlobInventory.of(carrier);
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
    public List<ReferenceMetaBlobInventory> allInventories() {
        return inventories.values().stream().map(ReferenceMetaBlobInventory::of).toList();
    }

    /**
     * @return The size of the shard. Each inventory counts as 1.
     */
    public int size() {
        return inventories.size();
    }
}
