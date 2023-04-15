package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.Experience;
import us.mytheria.bloblib.FuelAPI;
import us.mytheria.bloblib.entities.Fuel;
import us.mytheria.bloblib.entities.FurnaceOperation;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * A SuperFurnace is an instance of a SharableInventory.
 * It's a custom Furnace inventory that automatically smelts items.
 * Each operation will have a fuel size.
 * In order to make an operation, see {@link #handle()}.
 */
public class SuperFurnace<T extends InventoryButton> extends SharableInventory<T> {
    private T fuelButton;
    private T inputButton;
    private long operationSize = 200;
    private long storage = 0;
    private int outputSlot;
    private FurnaceOperation lastOperation;

    public static <T extends InventoryButton> SuperFurnace<T>
    fromInventoryBuilderCarrier(InventoryBuilderCarrier<T> carrier,
                                long operationSize) {
        ButtonManager<T> buttonManager = carrier.buttonManager();
        T fuelButton = buttonManager.getButton("Fuel");
        if (fuelButton == null)
            return null;
        if (fuelButton.getSlots().size() != 1)
            return null;

        T inputButton = buttonManager.getButton("Input");
        if (inputButton == null)
            return null;
        if (inputButton.getSlots().size() != 1)
            return null;

        T outputButton = buttonManager.getButton("Output");
        if (outputButton == null)
            return null;
        if (outputButton.getSlots().size() != 1)
            return null;
        int outputSlot = outputButton.getSlots().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Inventory '" + carrier.title() + "' has an empty " +
                                "Output button!"));
        return new SuperFurnace<>(carrier.title(), carrier.size(),
                buttonManager, operationSize, outputSlot,
                fuelButton, inputButton);
    }

    /**
     * Constructs a InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected SuperFurnace(@NotNull String title, int size,
                           @NotNull ButtonManager<T> buttonManager,
                           long operationSize,
                           int outputSlot,
                           @Nullable T fuelButton,
                           @Nullable T inputButton) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
        this.operationSize = operationSize;
        this.outputSlot = outputSlot;
        this.fuelButton = fuelButton;
        this.inputButton = inputButton;
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
        return new SuperFurnace<>(getTitle(), getSize(), getButtonManager(), getOperationSize(),
                getOutputSlot(), getFuelButton(), getInputButton());
    }

    /**
     * Will return the size of fuel required for each operation.
     *
     * @return The size of fuel required for each operation.
     */
    public long getOperationSize() {
        return operationSize;
    }

    /**
     * Will return the fuel stored in the current super furnace.
     *
     * @return The fuel stored in the current super furnace.
     */
    public long getStorage() {
        return storage;
    }

    /**
     * Will attempt to smelt the input item.
     *
     * @param fuel  The fuel to use.
     * @param input The input item to smelt.
     * @return The result of the operation.
     */
    public FurnaceOperation smelt(ItemStack fuel, ItemStack input) {
        int amount = input.getAmount();
        long operationSize = this.operationSize * amount;
        int compare = Long.compare(storage, operationSize);
        if (compare <= 0) {
            Result<Fuel> isFuel = FuelAPI.isFuel(fuel.getType());
            if (!isFuel.isValid())
                return FurnaceOperation.fail();
            Fuel value = isFuel.value();
            long ticks = value.getBurnTime() * amount;
            storage += ticks;
        }
        compare = Long.compare(storage, operationSize);
        if (compare == -1)
            return FurnaceOperation.fail();
        return FurnaceOperation.vanilla(input);
    }

    /**
     * @return The fuel ItemStack. Throws NoSuchElementException if the fuel button is empty.
     */
    public ItemStack getFuel() {
        return getButton(fuelButton.getSlots().stream().findFirst().orElseThrow());
    }

    /**
     * @return The input ItemStack. Throws NoSuchElementException if the input button is empty.
     */
    public ItemStack getInput() {
        return getButton(inputButton.getSlots().stream().findFirst().orElseThrow());
    }

    public int getOutputSlot() {
        return this.outputSlot;
    }

    /**
     * Will attempt to smelt the input item with the fuel.
     *
     * @return True if the operation was successful. False otherwise.
     */
    public boolean handle() {
        int outputSlot = getOutputSlot();
        if (lastOperation != null)
            return false;
        ItemStack fuel = getFuel();
        ItemStack input = getInput();

        FurnaceOperation operation = smelt(fuel, input);
        if (!operation.success())
            return false;
        ItemStack output = operation.result().clone();
        setButton(outputSlot, output);
        lastOperation = operation;
        return true;
    }

    /**
     * Will apply the experience to the player and remove the last operation.
     *
     * @param player The player to apply the experience to.
     */
    public void apply(Player player) {
        if (lastOperation == null)
            return;
        float experience = lastOperation.experience();
        Experience.changeExp(player, experience);
        lastOperation = null;
    }

    @Nullable
    public FurnaceOperation getLastOperation() {
        return this.lastOperation;
    }

    @NotNull
    public T getFuelButton() {
        return this.fuelButton;
    }

    @NotNull
    public T getInputButton() {
        return this.inputButton;
    }
}