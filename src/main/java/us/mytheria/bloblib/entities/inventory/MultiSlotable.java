package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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
    private final String permission;
    private final double price;
    private final String priceCurrency;

    /**
     * @param slots     The slots to be used.
     * @param slots     The slots to be used.
     * @param itemStack The ItemStack to be used.
     */
    public MultiSlotable(Collection<Integer> slots, ItemStack itemStack,
                         @Nullable String permission,
                         double price,
                         @Nullable String priceCurrency) {
        this.slots = slots;
        this.itemStack = itemStack;
        this.permission = permission;
        this.price = price;
        this.priceCurrency = priceCurrency;
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

    @Nullable
    public String getPermission() {
        return permission;
    }

    public double getPrice() {
        return price;
    }

    @Nullable
    public String getPriceCurrency() {
        return priceCurrency;
    }
}
