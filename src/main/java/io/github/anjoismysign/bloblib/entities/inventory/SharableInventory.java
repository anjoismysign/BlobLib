package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * A SharableInventory is an instance of an InventoryBuilder.
 * It's my own version/way for managing GUI's / inventories
 * using the Bukkit API.
 */
public class SharableInventory<T extends InventoryButton> extends InventoryBuilder<T> {
    private final Inventory inventory;

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
        this(title, size, buttonManager, null, null, null);
    }

    protected SharableInventory(@NotNull String title,
                                int size,
                                @NotNull ButtonManager<T> buttonManager,
                                @Nullable String reference,
                                @Nullable String locale,
                                @Nullable String path) {
        this.path = path;
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach((slot, supplier)->{
            setButton(slot,supplier.get());
        });
        this.loadDefaultButtons();
        this.key = reference;
        this.locale = locale;
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public SharableInventory<T> copy() {
        return new SharableInventory<>(getTitle(), getSize(), getButtonManager().copy(), key, locale, path);
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
     * Retrieves the items currently held in the inventory slots associated with a specific button key.
     *
     * @param key The unique identifier of the button/region.
     * @return A map where the keys are slot indices and values are the ItemStacks in those slots.
     * Returns an empty map if the key is not found.
     */
    @NotNull
    public Map<Integer, @Nullable ItemStack> getContents(@NotNull String key){
        Map<Integer, @Nullable ItemStack> map = new HashMap<>();
        @Nullable var button = getButton(key);
        if (button == null){
            return Map.of();
        }
        for (Integer slot : button.getSlots()) {
            map.put(slot, inventory.getItem(slot));
        }
        return map;
    }

    /**
     * Updates the items in the inventory for the slots associated with the specified button key.
     * <p>
     * If the contents map is null, all slots associated with the button will be cleared (set to null).
     * Otherwise, only the slots present in the map that are also owned by the button will be updated.
     * </p>
     *
     * @param key      The unique identifier of the button/region to update.
     * @param contents A map of slot indices and their corresponding ItemStacks.
     */
    public void setContents(@NotNull String key,
                            @Nullable Map<Integer, @Nullable ItemStack> contents){
        @Nullable var button = getButton(key);
        if (button == null){
            return;
        }
        if (contents == null){
            button.getSlots().forEach(slot->{
                inventory.setItem(slot, null);
            });
            return;
        }
        contents.entrySet()
                .stream()
                .filter(entry -> {
                    int slot = entry.getKey();
                    return button.containsSlot(slot);
                })
                .forEach(entry->{
                    int index = entry.getKey();
                    @Nullable ItemStack itemStack = entry.getValue();
                    inventory.setItem(index, itemStack);
                });
    }

}
