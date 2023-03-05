package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public abstract class InventoryBuilder<T extends InventoryButton> {
    private String title;
    private int size;
    private ButtonManager<T> buttonManager;

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

    public ButtonManager<T> getButtonManager() {
        return buttonManager;
    }

    public void setButtonManager(ButtonManager<T> buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Nullable
    public Set<Integer> getSlots(String key) {
        return buttonManager.get(key);
    }

    public ItemStack getButton(int slot) {
        return buttonManager.get(slot);
    }

    public T getButton(String key) {
        return buttonManager.getButton(key);
    }

    public Collection<String> getKeys() {
        return buttonManager.keys();
    }

    public boolean isInsideButton(String key, int slot) {
        T button = getButton(key);
        if (button == null) throw new NullPointerException("Button with key '" + key + "' does not exist!");
        return button.containsSlot(slot);
    }
}
