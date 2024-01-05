package us.mytheria.bloblib.events;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.entities.translatable.TranslatableItem;

/**
 * Called when a TranslatableItem is cloned.
 */
public class TranslatableItemCloneEvent extends TranslatableItemEvent {
    private ItemStack clone;

    /**
     * Called when a TranslatableItem is cloned.
     *
     * @param translatableItem The TranslatableItem.
     * @param clone            The clone.
     */
    public TranslatableItemCloneEvent(TranslatableItem translatableItem, ItemStack clone) {
        super(translatableItem, false);
        this.clone = clone;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Will get the clone.
     *
     * @return The clone.
     */
    public ItemStack getClone() {
        return clone;
    }

    /**
     * Will set the clone.
     *
     * @param clone The clone.
     */
    public void setClone(ItemStack clone) {
        this.clone = clone;
    }
}
