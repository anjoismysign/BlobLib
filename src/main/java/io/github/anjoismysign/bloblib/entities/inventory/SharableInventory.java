package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        getButtonManager().getIntegerKeys().forEach((slot, supplier)->{
            setButton(slot,supplier.get());
        });
        this.loadDefaultButtons();
        this.reference = reference;
        this.locale = locale;
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public SharableInventory<T> copy() {
        return new SharableInventory<>(getTitle(), getSize(), getButtonManager().copy(), reference, locale);
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

}
