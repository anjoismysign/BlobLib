package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.action.ActionMemo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author anjoismysign
 * <p>
 * A MultiSlotable is a concept of an ItemStack which can
 * be placed in multiple slots, lets say slots of an
 * inventory/GUI.
 */
public abstract class MultiSlotable {
    @NotNull
    private final Collection<Integer> slots;
    @NotNull
    private final ItemStack itemStack;
    private final boolean isPermissionInverted;
    @Nullable
    private final String hasPermission;
    private final boolean isMoneyInverted;
    private final double hasMoney;
    private final boolean isTranslatableItemInverted;
    private final String hasTranslatableItem;
    @Nullable
    private final String moneyCurrency;
    @NotNull
    private final List<ActionMemo> actions;

    /**
     * @param slots                      The slots to be used.
     * @param itemStack                  The ItemStack to be used.
     * @param hasPermission              If the player needs to have a permission.
     * @param hasMoney                   If the player needs to have money.
     * @param priceCurrency              The price currency to be used.
     * @param actions                    The actions to be used.
     * @param hasTranslatableItem        If the player needs to have a TranslatableItem.
     * @param isPermissionInverted       If the permission check is inverted.
     * @param isMoneyInverted            If the money check is inverted.
     * @param isTranslatableItemInverted If the TranslatableItem check is inverted.
     */
    public MultiSlotable(@NotNull Collection<Integer> slots,
                         @NotNull ItemStack itemStack,
                         @Nullable String hasPermission,
                         double hasMoney,
                         @Nullable String priceCurrency,
                         @NotNull List<ActionMemo> actions,
                         @Nullable String hasTranslatableItem,
                         boolean isPermissionInverted,
                         boolean isMoneyInverted,
                         boolean isTranslatableItemInverted) {
        this.slots = slots;
        this.itemStack = itemStack;
        this.hasPermission = hasPermission;
        this.hasMoney = hasMoney;
        this.moneyCurrency = priceCurrency;
        this.actions = actions;
        this.hasTranslatableItem = hasTranslatableItem;
        this.isPermissionInverted = isPermissionInverted;
        this.isMoneyInverted = isMoneyInverted;
        this.isTranslatableItemInverted = isTranslatableItemInverted;
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
    public String getHasPermission() {
        return hasPermission;
    }

    public double getHasMoney() {
        return hasMoney;
    }

    @Nullable
    public String getHasTranslatableItem() {
        return hasTranslatableItem;
    }

    @Nullable
    public String getMoneyCurrency() {
        return moneyCurrency;
    }

    @NotNull
    public List<ActionMemo> getActions() {
        return actions;
    }

    public boolean isPermissionInverted() {
        return isPermissionInverted;
    }

    public boolean isMoneyInverted() {
        return isMoneyInverted;
    }

    public boolean isTranslatableItemInverted() {
        return isTranslatableItemInverted;
    }
}
