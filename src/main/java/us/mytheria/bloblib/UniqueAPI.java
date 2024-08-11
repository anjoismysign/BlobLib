package us.mytheria.bloblib;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UniqueAPI {
    private static UniqueAPI instance;
    private final NamespacedKey uniqueItemKey;

    private UniqueAPI(BlobLib blobLib) {
        this.uniqueItemKey = new NamespacedKey(blobLib, "unique");
    }

    protected static UniqueAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            UniqueAPI.instance = new UniqueAPI(blobLib);
        }
        return instance;
    }

    public static UniqueAPI getInstance() {
        return getInstance(null);
    }

    /**
     * Sets a PersistentDataHolder to be unique alike.
     */
    public void setUnique(PersistentDataHolder holder) {
        UUID random = UUID.randomUUID();
        holder.getPersistentDataContainer().set(uniqueItemKey, PersistentDataType.STRING, random.toString());
    }

    /**
     * @param itemStack The item you want to set as a unique alike item.
     * @return true if successful, false if not.
     */
    public boolean setUnique(ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        setUnique(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return true;
    }

    /**
     * Will set all ItemStack inside array to be unique alike.
     *
     * @param itemStacks The array you want to set their content a unique alike.
     */
    public void setUnique(ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            setUnique(itemStack);
        }
    }

    /**
     * Will set all ItemStack inside inventory to be unique alike.
     *
     * @param inventory The inventory you want to set all items inside to be unique alike.
     */
    public void setUnique(Inventory inventory) {
        setUnique(inventory.getContents());
    }

    /**
     * Will set all ItemStack from inventory holder to be unique alike.
     *
     * @param inventoryHolder The inventory holder you want to set all items inside to be unique alike.
     */
    public void setUnique(InventoryHolder inventoryHolder) {
        setUnique(inventoryHolder.getInventory());
    }

    /**
     * Will set all ItemStack inside collection to be unique alike.
     *
     * @param itemStacks The items you want set unique alike.
     */
    public void setUnique(Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            setUnique(itemStack);
        }
    }

    /**
     * @param holder The PersistentDataHolder you want to check.
     * @return true if the PersistentDataHolder is unique alike, false if not.
     */
    public boolean isUnique(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        return container.has(uniqueItemKey, PersistentDataType.STRING);
    }

    /**
     * @param itemStack The item you want to check.
     * @return true if the item is unique alike, false if not.
     */
    public boolean isUnique(ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return isUnique(itemMeta);
    }

    /**
     * @param inventory The inventory you want to check.
     * @return A list of all unique alike items inside the inventory.
     */
    public List<ItemStack> getUniques(Inventory inventory) {
        List<ItemStack> uniques = new ArrayList<>();
        for (ItemStack itemStack : inventory.getContents()) {
            if (isUnique(itemStack)) {
                uniques.add(itemStack);
            }
        }
        return uniques;
    }

    /**
     * @param inventoryHolder The inventory holder you want to check.
     * @return A list of all unique alike items inside the inventory holder.
     */
    public List<ItemStack> getUniques(InventoryHolder inventoryHolder) {
        return getUniques(inventoryHolder.getInventory());
    }
}
