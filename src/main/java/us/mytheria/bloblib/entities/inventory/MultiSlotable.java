package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.action.ActionType;

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
    @Nullable
    private final String action;
    @Nullable
    private final ActionType actionType;

    /**
     * @param slots         The slots to be used.
     * @param itemStack     The ItemStack to be used.
     * @param permission    The permission to be used.
     * @param price         The price to be used.
     * @param priceCurrency The price currency to be used.
     * @param action        The action to be used.
     * @param actionType    The action type to be used.
     */
    public MultiSlotable(Collection<Integer> slots, ItemStack itemStack,
                         @Nullable String permission,
                         double price,
                         @Nullable String priceCurrency,
                         @Nullable String action,
                         @Nullable ActionType actionType) {
        this.slots = slots;
        this.itemStack = itemStack;
        this.permission = permission;
        this.price = price;
        this.priceCurrency = priceCurrency;
        this.action = action;
        this.actionType = actionType;
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

    @Nullable
    public String getAction() {
        return action;
    }

    @Nullable
    public ActionType getActionType() {
        return actionType;
    }
}
