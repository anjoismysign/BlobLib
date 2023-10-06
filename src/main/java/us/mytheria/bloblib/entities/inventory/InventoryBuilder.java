package us.mytheria.bloblib.entities.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.itemstack.ItemStackModder;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

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

    @Nullable
    public T getButton(String key) {
        return buttonManager.getButton(key);
    }

    /**
     * Will modify the ItemStacks of the button.
     *
     * @param key      The key of the button.
     * @param consumer The consumer to modify the ItemStacks.
     */
    public void modify(String key, Consumer<ItemStack> consumer) {
        T button = getButton(key);
        if (button != null)
            button.getSlots().forEach(slot -> {
                ItemStack item = getButton(slot);
                if (item != null)
                    consumer.accept(item);
            });
    }

    /**
     * Will modify the ItemStacks of the button with an ItemStackModder.
     *
     * @param key      The key of the button.
     * @param consumer The consumer to modify the ItemStacks.
     */
    public void modder(String key, Consumer<ItemStackModder> consumer) {
        modify(key, item -> consumer.accept(ItemStackModder.mod(item)));
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
}
