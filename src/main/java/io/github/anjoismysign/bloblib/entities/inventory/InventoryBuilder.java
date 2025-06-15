package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public abstract class InventoryBuilder<T extends InventoryButton> {
    private String title;
    private int size;
    private ButtonManager<T> buttonManager;
    @Nullable
    protected String reference;
    @Nullable
    protected String locale;

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

    @Nullable
    public T getButton(String key) {
        return buttonManager.getButton(key);
    }

    public Collection<String> getKeys() {
        return buttonManager.keys();
    }

    public boolean isInsideButton(String key, int slot) {
        T button = getButton(key);
        if (button == null) {
            BlobLib.getAnjoLogger().singleError("InventoryButton with key '" + key + "' inside " +
                    "inventory '" + getTitle() + "' does not exist!");
            return false;
        }
        return button.containsSlot(slot);
    }

    /**
     * Will handle both the permission and payment of the button.
     * Should always be checked!
     *
     * @param key    The key of the button.
     * @param player The player to handle the permission and payment for.
     * @return Whether the permission and payment was handled successfully.
     */
    public boolean handleAll(String key, Player player) {
        T button = getButton(key);
        if (button == null) {
            BlobLib.getAnjoLogger().singleError("InventoryButton with key '" + key + "' inside " +
                    "inventory '" + getTitle() + "' does not exist!");
            return false;
        }
        return button.handleAll(player);
    }

    @Nullable
    public String getLocale() {
        return locale;
    }

    @Nullable
    public String getReference() {
        return reference;
    }
}
