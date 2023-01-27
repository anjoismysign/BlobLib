package us.mytheria.bloblib.entities;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * @author anjoismysign
 * <p>
 * A MultiSlotable is a concept of an ItemStack which can
 * be placed in multiple slots, lets say slots of an
 * inventory/GUI.
 */
public abstract class MultiSlotable {
    private final Collection<Integer> slots;
    private final ItemStack itemStack;

    /**
     * @param slots     The slots to be used.
     * @param slots     The slots to be used.
     * @param itemStack The ItemStack to be used.
     */
    public MultiSlotable(Collection<Integer> slots, ItemStack itemStack) {
        this.slots = slots;
        this.itemStack = itemStack;
    }

    /**
     * Retrieves the slots of which this MultiSlotable is linked to.
     *
     * @return The slots of which this MultiSlotable is linked to.
     */
    public Collection<Integer> getSlots() {
        return slots;
    }

    /**
     * @return The ItemStack of this MultiSlotable.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
}
