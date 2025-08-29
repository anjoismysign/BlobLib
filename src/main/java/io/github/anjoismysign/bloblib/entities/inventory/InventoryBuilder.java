package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.itemstack.ItemStackModder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class InventoryBuilder<T extends InventoryButton> {
    private String title;
    private int size;
    private ButtonManager<T> buttonManager;
    @Nullable
    protected String reference;
    @Nullable
    protected String locale;
    protected final Map<String, ItemStack> defaultButtons = new HashMap<>();

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

    /**
     * Adds a default button using the specified key.
     * Default buttons are meant to persist inventory
     * changes.
     *
     * @param key The key of the button
     */
    public void addDefaultButton(String key) {
        for (Integer i : getSlots(key)) {
            addDefaultButton(key, getButton(i));
            break;
        }
    }

    /**
     * Loads/reloads the default buttons.
     */
    public void loadDefaultButtons() {
        defaultButtons.clear();
        getKeys().forEach(this::addDefaultButton);
    }

    /**
     * Adds a default button to the memory of the inventory.
     *
     * @param name The name of the button
     * @param item The ItemStack of the button
     */
    public void addDefaultButton(String name, ItemStack item) {
        defaultButtons.put(name, item);
    }

    /**
     * Searches for the ItemStack that is linked to said key.
     *
     * @param key The key of the button
     * @return The ItemStack
     */
    @Nullable
    public ItemStack getDefaultButton(String key) {
        return defaultButtons.get(key);
    }

    /**
     * Searches for the ItemStack that is linked to said key.
     * The ItemStack is cloned and then returned.
     *
     * @param key The key of the button
     * @return The cloned ItemStack
     */
    @Nullable
    public ItemStack cloneDefaultButton(String key) {
        return defaultButtons.get(key).clone();
    }

    /**
     * Retrieves the default buttons
     *
     * @return The default buttons
     */
    @NotNull
    public Map<String, ItemStack> getDefaultButtons() {
        return defaultButtons;
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

    /**
     * Builds the inventory since by default its reference is null
     */
    public void buildInventory() {
        @Nullable Inventory inventory = getInventory();
        Objects.requireNonNull(inventory, "Inventory cannot be null!");
        getButtonManager().getIntegerKeys().forEach((slot, supplier)->{
            setButton(slot, supplier.get());
        });
    }

    /**
     * Retrieves the inventory
     *
     * @return The inventory if is not null, otherwise null.
     */
    @Nullable
    abstract Inventory getInventory();

    /**
     * Sets the button at the specified slot
     *
     * @param slot      The slot to set the button at
     * @param itemStack The ItemStack to set the button to
     */
    public void setButton(int slot, ItemStack itemStack) {
        @Nullable Inventory inventory = getInventory();
        if (inventory == null){
            return;
        }
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(BlobLib.getInstance(), () -> {
                setButton(slot, itemStack);
            });
            return;
        }
        try {
            getInventory().setItem(slot, itemStack);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Refills all ItemStacks that belong to a said key
     *
     * @param key The key of the button
     */
    public void refillButton(String key) {
        @Nullable Set<Integer> slots = getSlots(key);
        if (slots == null){
            return;
        }
        for (Integer index : slots) {
            setButton(index, getButton(index));
        }
    }

    public void open(Player player) {
        @Nullable Inventory inventory = getInventory();
        Objects.requireNonNull(inventory, "Inventory is null!");
        player.openInventory(inventory);
    }

    /**
     * Will modify the ItemStacks of the button.
     * Allows replacing the entire ItemStack.
     *
     * @param key      The key of the button.
     * @param function The consumer to modify the ItemStacks.
     */
    public void modify(String key, Function<ItemStack, ItemStack> function) {
        T button = getButton(key);
        if (button != null)
            button.getSlots().forEach(slot -> {
                ItemStack item = getButton(slot);
                if (item != null) {
                    ItemStack result = function.apply(item);
                    setButton(slot, result);
                }
            });
    }

    /**
     * Will modify the ItemStacks of the button with an ItemStackModder.
     * Doesn't allow replacing the entire ItemStack.
     *
     * @param key      The key of the button.
     * @param consumer The consumer to modify the ItemStacks.
     */
    public void modder(String key, Consumer<ItemStackModder> consumer) {
        modify(key, item -> {
            consumer.accept(ItemStackModder.mod(item));
            return item;
        });
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
