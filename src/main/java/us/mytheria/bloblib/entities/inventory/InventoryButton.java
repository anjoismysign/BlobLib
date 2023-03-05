package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InventoryButton {
    private final String key;
    private final Set<Integer> slots;

    public InventoryButton(String key, Set<Integer> slots) {
        this.key = key;
        this.slots = slots;
    }

    public String getKey() {
        return key;
    }

    public Set<Integer> getSlots() {
        return slots;
    }

    public void setDisplay(ItemStack display, ButtonManager<?> manager) {
        slots.forEach(slot -> manager.getIntegerKeys().put(slot, display));
    }

    public void setDisplay(ItemStack display, SharableInventory<?> inventory) {
        slots.forEach(slot -> {
            inventory.getButtonManager().getIntegerKeys().put(slot, display);
            inventory.setButton(slot, display);
        });
    }

    public boolean containsSlot(int slot) {
        return slots.contains(slot);
    }
}
