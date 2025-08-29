package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * An InventoryHolderBuilder is an instance of an InventoryBuilder.
 * It's a wrapper for InventoryHolder's using the BlobLib API.
 */
public class InventoryHolderBuilder<T extends InventoryButton> extends InventoryBuilder<T> {
    private final InventoryHolder holder;

    /**
     * Constructs an InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected InventoryHolderBuilder(@NotNull String title, int size,
                                     @NotNull ButtonManager<T> buttonManager,
                                     @NotNull InventoryHolder holder) {
        this.holder = Objects.requireNonNull(holder, "'holder' cannot be null");
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public InventoryHolderBuilder<T> copy() {
        return new InventoryHolderBuilder<>(getTitle(), getSize(), getButtonManager(), getHolder());
    }

    /**
     * Retrieves the inventory
     *
     * @return The inventory if the holder is not null, otherwise null.
     */
    @Nullable
    public Inventory getInventory() {
        InventoryHolder holder = getHolder();
        return holder.getInventory();
    }

    /**
     * Retrieves the inventory holder
     *
     * @return The inventory holder
     */
    @NotNull
    public InventoryHolder getHolder() {
        return holder;
    }
}
