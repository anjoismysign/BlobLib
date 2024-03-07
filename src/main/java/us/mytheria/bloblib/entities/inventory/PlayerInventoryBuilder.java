package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anjoismysign
 * <p>
 * An InventoryHolderBuilder is an instance of a InventoryBuilder.
 * It's a wrapper for InventoryHolder's using the BlobLib API.
 */
public class PlayerInventoryBuilder<T extends InventoryButton> extends InventoryBuilder<T> {
    private final UUID holderId;
    private HashMap<String, ItemStack> defaultButtons;

    /**
     * Constructs a InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected PlayerInventoryBuilder(@NotNull String title, int size,
                                     @NotNull ButtonManager<T> buttonManager,
                                     @Nullable UUID holderId) {
        Player player = Bukkit.getPlayer(holderId);
        if (player == null)
            throw new IllegalArgumentException("Player with UUID " + holderId + " is not online!");
        this.holderId = holderId;
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Creates a new InventoryHolderBuilder in an empty constructor.
     * <p>
     * I recommend you try constructing your InventoryHolderBuilder
     * and get used to the API for a strong workflow.
     *
     * @param playerId The inventory holder
     */
    public PlayerInventoryBuilder(UUID playerId) {
        this.holderId = playerId;
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public PlayerInventoryBuilder<T> copy() {
        return new PlayerInventoryBuilder<>(getTitle(), getSize(), getButtonManager(), getHolderId());
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(holderId);
    }

    /**
     * Will create a new InventoryHolderBuilder with the same
     * title, size, and ButtonManager with provided holder.
     *
     * @param holderId The inventory holder
     * @return The new InventoryHolderBuilder
     */
    @NotNull
    public PlayerInventoryBuilder<T> setHolderId(@NotNull UUID holderId) {
        Objects.requireNonNull(holderId, "'holder' cannot be null!");
        return new PlayerInventoryBuilder<>(getTitle(), getSize(), getButtonManager(), holderId);
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
        if (getInventory() == null)
            throw new IllegalStateException("PlayerInventory is null!");
        getButtonManager().getIntegerKeys().forEach(this::setButton);
    }

    /**
     * Retrieves the inventory
     *
     * @return The inventory if holder is not null, otherwise null.
     */
    @Nullable
    public Inventory getInventory() {
        Player holder = getPlayer();
        if (holder == null)
            return null;
        return holder.getInventory();
    }

    /**
     * Retrieves the inventory holder unique id
     *
     * @return The UUID
     */
    @Nullable
    public UUID getHolderId() {
        return holderId;
    }

    /**
     * Sets the button at the specified slot
     *
     * @param slot      The slot to set the button at
     * @param itemStack The ItemStack to set the button to
     */
    public void setButton(int slot, ItemStack itemStack) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("BlobLib"), () -> getInventory().setItem(slot, itemStack));
            return;
        }
        getInventory().setItem(slot, itemStack);
    }

    /**
     * Refills all ItemStacks that belong to said key
     *
     * @param key The key of the button
     */
    public void refillButton(String key) {
        for (Integer i : getSlots(key)) {
            setButton(i, getButton(i));
        }
    }
}