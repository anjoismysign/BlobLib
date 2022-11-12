package us.mytheria.bloblib.objects;

import org.bukkit.inventory.ItemStack;

public class Slotable {
    private int slot;
    private ItemStack itemStack;

    public Slotable(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
