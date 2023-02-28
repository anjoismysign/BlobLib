package us.mytheria.bloblib.entities.inventory;

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
}
