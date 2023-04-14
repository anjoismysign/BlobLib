package us.mytheria.bloblib.entities.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * A SuperFurnace is an instance of a SharableInventory.
 * It's a custom Furnace inventory that automatically smelts items.
 * Each operation will have a fuel size.
 */
public class SuperFurnace<T extends InventoryButton> extends SharableInventory<T> {
    private HashMap<String, ItemStack> defaultButtons;
    private long operationSize = 200;

    /**
     * Constructs a InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected SuperFurnace(@NotNull String title, int size,
                           @NotNull ButtonManager<T> buttonManager,
                           long operationSize) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
        this.operationSize = operationSize;
    }

    /**
     * Creates a new InventoryHolderBuilder in an empty constructor.
     * Default operation size is 200.
     * <p>
     * I recommend you try constructing your InventoryHolderBuilder
     * and get used to the API for a strong workflow.
     */
    public SuperFurnace() {
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public SuperFurnace<T> copy() {
        return new SuperFurnace<>(getTitle(), getSize(), getButtonManager(), getOperationSize());
    }

    public long getOperationSize() {
        return operationSize;
    }

    public boolean smelt(ItemStack itemStack) {

    }
}
