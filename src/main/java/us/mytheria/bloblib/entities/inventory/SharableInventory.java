package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.itemstack.ItemStackModder;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
    protected SharableInventory(@NotNull String title,
                                int size,
                                @NotNull ButtonManager<T> buttonManager) {
        this(title, size, buttonManager, null, null);
    }

    protected SharableInventory(@NotNull String title,
                                int size,
                                @NotNull ButtonManager<T> buttonManager,
                                @Nullable String reference,
                                @Nullable String locale) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
        this.reference = reference;
        this.locale = locale;
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
        return new SharableInventory<>(getTitle(), getSize(), getButtonManager().copy());
    }

    /**
     * Adds a default button using the specified key.
     * Default buttons are meant to persist inventory
     * changes.
     *
     * @param key The key of the button
     */
    public SharableInventory<T> addDefaultButton(String key) {
        for (Integer i : getSlots(key)) {
            addDefaultButton(key, getButton(i));
            break;
        }
        return this;
    }

    /**
     * Loads/reloads the default buttons.
     */
    public SharableInventory<T> loadDefaultButtons() {
        setDefaultButtons(new HashMap<>());
        ButtonManager<T> manager = getButtonManager();
        getKeys().forEach(this::addDefaultButton);
        return this;
    }

    /**
     * Adds a default button to the memory of the inventory.
     *
     * @param name The name of the button
     * @param item The ItemStack of the button
     */
    public SharableInventory<T> addDefaultButton(String name, ItemStack item) {
        defaultButtons.put(name, item);
        return this;
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
    public SharableInventory<T> setDefaultButtons(HashMap<String, ItemStack> defaultButtons) {
        this.defaultButtons = defaultButtons;
        return this;
    }

    /**
     * Builds the inventory since by default its reference is null
     */
    public SharableInventory<T> buildInventory() {
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach(this::setButton);
        return this;
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
    public SharableInventory<T> setInventory(Inventory inventory) {
        this.inventory = inventory;
        return this;
    }

    /**
     * Sets the button at the specified slot
     *
     * @param slot      The slot to set the button at
     * @param itemStack The ItemStack to set the button to
     */
    public SharableInventory<T> setButton(int slot, ItemStack itemStack) {
        try {
            inventory.setItem(slot, itemStack);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return this;
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

    /**
     * Refills all ItemStacks that belong to said key
     *
     * @param key The key of the button
     */
    public SharableInventory<T> refillButton(String key) {
        Set<Integer> set = getSlots(key);
        if (set == null)
            return this;
        for (Integer i : set) {
            setButton(i, getButton(i));
        }
        return this;
    }

    public SharableInventory<T> open(Player player) {
        player.openInventory(inventory);
        return this;
    }
}
