package us.mytheria.bloblib;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SoulAPI {
    private static SoulAPI instance;
    private final NamespacedKey soulItemKey;

    private SoulAPI(BlobLib blobLib) {
        this.soulItemKey = new NamespacedKey(blobLib, "soul");
    }

    protected static SoulAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            SoulAPI.instance = new SoulAPI(blobLib);
        }
        return instance;
    }

    public static SoulAPI getInstance() {
        return getInstance(null);
    }

    /**
     * Sets a PersistentDataHolder to be soul alike.
     */
    public void setSoul(PersistentDataHolder holder) {
        holder.getPersistentDataContainer().set(soulItemKey, PersistentDataType.BYTE, (byte) 1);
    }

    /**
     * @param itemStack The item you want to set as a soul alike item.
     * @return true if succesful, false if not.
     */
    public boolean setSoul(ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        setSoul(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return true;
    }

    /**
     * Sets all items in the ItemStack array to be soul alike.
     *
     * @param array The ItemStack array you want to set the items as soul alike.
     */
    public void setSoul(ItemStack[] array) {
        for (ItemStack itemStack : array) {
            setSoul(itemStack);
        }
    }

    /**
     * Sets all items in the inventory to be soul alike.
     *
     * @param player The player you want to set the items as soul alike.
     */
    public void setSoul(Player player) {
        setSoul(player.getInventory().getContents());
        setSoul(player.getInventory().getArmorContents());
    }

    /**
     * @param holder The PersistentDataHolder you want to check.
     * @return true if the PersistentDataHolder is soul alike, false if not.
     */
    public boolean isSoul(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(soulItemKey, PersistentDataType.BYTE))
            return false;
        Byte b = container.get(soulItemKey, PersistentDataType.BYTE);
        return b != null && b == 1;
    }

    /**
     * @param itemStack The item you want to check.
     * @return true if the item is soul alike, false if not.
     */
    public boolean isSoul(ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return isSoul(itemMeta);
    }

    /**
     * Will drop all non soul alike items from the inventory.
     *
     * @param inventoryHolder The inventory holder you want to drop the items from.
     * @param dropLocation    The location where the items will be dropped.
     */
    public void dropNonSouls(InventoryHolder inventoryHolder, Location dropLocation) {
        Inventory inventory = inventoryHolder.getInventory();
        for (ItemStack itemStack : inventoryHolder.getInventory().getContents()) {
            if (itemStack == null)
                continue;
            if (isSoul(itemStack)) continue;
            dropLocation.getWorld().dropItemNaturally(dropLocation, itemStack.clone());
            itemStack.setAmount(0);
        }
    }

    /**
     * Will drop all non soul alike items from the inventory.
     *
     * @param player The player you want to drop the items from.
     */
    public void dropNonSouls(Player player) {
        dropNonSouls(player, player.getLocation());
    }

    /**
     * @param inventory The inventory you want to check.
     * @return A list of all soul alike items in the inventory.
     */
    public List<ItemStack> getSouls(Inventory inventory) {
        List<ItemStack> souls = new ArrayList<>();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null)
                continue;
            if (isSoul(itemStack)) souls.add(itemStack);
        }
        return souls;
    }

    /**
     * @param inventoryHolder The inventory holder you want to check.
     * @return A list of all soul alike items from inventory holder.
     */
    public List<ItemStack> getSouls(InventoryHolder inventoryHolder) {
        return getSouls(inventoryHolder.getInventory());
    }
}
