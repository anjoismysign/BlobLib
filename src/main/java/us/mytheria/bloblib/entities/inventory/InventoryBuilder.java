package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public abstract class InventoryBuilder {
    private String title;
    private int size;
    private ButtonManager buttonManager;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public void setButtonManager(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    public Set<Integer> getSlots(String key) {
        return buttonManager.get(key);
    }

    public ItemStack getButton(int slot) {
        return buttonManager.get(slot);
    }

    public Collection<String> getKeys() {
        return buttonManager.keys();
    }
}
