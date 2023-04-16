package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author anjoismysign
 * <p>
 * A SharableInventory is an instance of a InventoryBuilder.
 * It's my own version/way for managing GUI's / inventories
 * using the Bukkit API.
 */
public class SharableInventory<T extends InventoryButton> extends InventoryBuilder<T> {
    private Inventory inventory;
    private HashMap<String, ItemStack> defaultButtons;

    /**
     * Constructs a SharableInventory with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected SharableInventory(@NotNull String title, int size,
                                @NotNull ButtonManager<T> buttonManager) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Creates a new SharableInventory in an empty constructor.
     * <p>
     * I recommend you try constructing your SharableInventory
     * and get used to the API for a strong workflow.
     */
    public SharableInventory() {
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public SharableInventory<T> copy() {
        return new SharableInventory<>(getTitle(), getSize(), getButtonManager());
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
        setDefaultButtons(new HashMap<>());
        ButtonManager<T> manager = getButtonManager();
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
    public HashMap<String, ItemStack> getDefaultButtons() {
        return defaultButtons;
    }

    /**
     * Sets the default buttons
     *
     * @param defaultButtons The default buttons to set
     */
    public void setDefaultButtons(HashMap<String, ItemStack> defaultButtons) {
        this.defaultButtons = defaultButtons;
    }

    /**
     * Builds the inventory since by default its reference is null
     */
    public void buildInventory() {
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach(this::setButton);
    }

    /**
     * Retrieves the inventory
     *
     * @return The inventory
     */
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the inventory
     *
     * @param inventory The inventory to set
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Sets the button at the specified slot
     *
     * @param slot      The slot to set the button at
     * @param itemStack The ItemStack to set the button to
     */
    public void setButton(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    /**
     * Refills all ItemStacks that belong to said key
     *
     * @param key The key of the button
     */
    public void refillButton(String key) {
        Set<Integer> set = getSlots(key);
        if (set == null)
            return;
        for (Integer i : set) {
            setButton(i, getButton(i));
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
