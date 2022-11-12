package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public abstract class MultiSlotable {
    private Collection<Integer> slots;
    private ItemStack itemStack;

    public MultiSlotable(Collection<Integer> slots, ItemStack itemStack) {
        this.slots = slots;
        this.itemStack = itemStack;
    }

    public Collection<Integer> getSlots() {
        return slots;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
