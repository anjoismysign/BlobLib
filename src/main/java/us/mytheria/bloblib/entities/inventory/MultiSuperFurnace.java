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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anjoismysign
 * <p>
 * A MultiSuperFurnace is an instance of a SharableInventory.
 * It's a custom Furnace inventory that automatically smelts items.
 * Each operation will have a fuel size.
 * In order to make an operation, see {@link #handle()}.
 * The difference between a normal SuperFurnace and a MultiSuperFurnace is that
 * a MultiSuperFurnace can have multiple fuel, input and output slots.
 */
public class MultiSuperFurnace<T extends InventoryButton> extends SharableInventory<T> {
    private T fuelButton;
    private T inputButton;
    private T outputButton;
    private long operationSize = 200;
    private long storage = 0;
    private Map<Integer, FurnaceOperation> operationHistory;
    private final FuelAPI fuelAPI;

    public static <T extends InventoryButton> MultiSuperFurnace<T>
    fromInventoryBuilderCarrier(InventoryBuilderCarrier<T> carrier,
                                long operationSize) {
        ButtonManager<T> buttonManager = carrier.buttonManager();
        T fuelButton = buttonManager.getButton("Fuel");
        if (fuelButton == null)
            return null;

        T inputButton = buttonManager.getButton("Input");
        if (inputButton == null)
            return null;

        T outputButton = buttonManager.getButton("Output");
        if (outputButton == null)
            return null;
        int outputSlot = outputButton.getSlots().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Inventory '" + carrier.title() + "' has an empty " +
                                "Output button!"));
        return new MultiSuperFurnace<>(carrier.title(), carrier.size(),
                buttonManager, operationSize, fuelButton, inputButton, outputButton);
    }

    /**
     * Constructs a InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected MultiSuperFurnace(@NotNull String title, int size,
                                @NotNull ButtonManager<T> buttonManager,
                                long operationSize,
                                @NotNull T fuelButton,
                                @NotNull T inputButton,
                                @NotNull T outputButton) {
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
        this.operationHistory = new HashMap<>();
        this.operationSize = operationSize;
        this.fuelButton = fuelButton;
        this.inputButton = inputButton;
        this.outputButton = outputButton;
        this.fuelAPI = FuelAPI.getInstance();
    }

    /**
     * Creates a new InventoryHolderBuilder in an empty constructor.
     * Default operation size is 200.
     * <p>
     * I recommend you try constructing your InventoryHolderBuilder
     * and get used to the API for a strong workflow.
     */
    public MultiSuperFurnace() {
        this.fuelAPI = FuelAPI.getInstance();
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public MultiSuperFurnace<T> copy() {
        return new MultiSuperFurnace<>(getTitle(), getSize(),
                getButtonManager().copy(), getOperationSize(),
                getFuelButton(), getInputButton(), getOutputButton());
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
     * Will attempt to smelt all input items with all provided fuel.
     * If at least one input item cannot be smelted, the operation will fail.
     *
     * @param fuel  The fuel to use.
     * @param input The input items to smelt.
     * @return The result of the operation.
     */
    @Nullable
    public List<FurnaceOperation> smelt(ItemStack[] fuel, ItemStack[] input) {
        List<FurnaceOperation> list = new ArrayList<>();
        long operationSize = this.operationSize;
        for (ItemStack inputItem : input) {
            operationSize += inputItem.getAmount() * this.operationSize;
        }
        int compare = Long.compare(storage, operationSize);
        if (compare <= 0) {
            for (ItemStack fuelItem : fuel) {
                Result<Fuel> isFuel = fuelAPI.isFuel(fuelItem.getType());
                if (!isFuel.isValid())
                    continue;
                Fuel value = isFuel.value();
                long ticks = value.getBurnTime() * fuelItem.getAmount();
                storage += ticks;
            }
        }
        compare = Long.compare(storage, operationSize);
        if (compare == -1)
            return null;
        for (ItemStack inputItem : input) {
            list.add(FurnaceOperation.vanilla(inputItem));
        }
        if (!canHandle(list.size()))
            return null;
        return list;
    }

    /**
     * @return Gets the fuel ItemStacks, including empty slots.
     */
    public ItemStack[] getFuel() {
        return fuelButton.getSlots().stream().map(this::getButton).toArray(ItemStack[]::new);
    }

    /**
     * @return Gets the input ItemStacks, including empty slots.
     */
    public ItemStack[] getInput() {
        return inputButton.getSlots().stream().map(this::getButton).toArray(ItemStack[]::new);
    }

    /**
     * @return Gets the output slots, including non-empty slots.
     */
    public Set<Integer> getOutputSlots() {
        return outputButton.getSlots();
    }

    public boolean canHandle(int amount) {
        return getOutputSlots().stream().map(this::getButton).filter(itemStack -> itemStack == null
                || itemStack != null && itemStack.getType().isAir()).count() >= amount;
    }

    /**
     * Will attempt to smelt the input item with the fuel.
     *
     * @return True if the operation was successful. False otherwise.
     */
    public boolean handle() {
        Set<Integer> available = getAvailableSlots();
        ItemStack[] fuel = getFuel();
        ItemStack[] input = getInput();

        List<FurnaceOperation> operations = smelt(fuel, input);
        if (operations == null)
            return false;
        operations.forEach(operation -> {
            int slot = available.stream().findFirst().orElseThrow();
            available.remove(slot);
            operationHistory.put(slot, operation);
            ItemStack output = operation.result().clone();
            setButton(slot, output);
        });
        return true;
    }

    /**
     * @return The available slots for the next operation.
     */
    @NotNull
    public Set<Integer> getAvailableSlots() {
        Set<Integer> dupe = new HashSet<>(getOutputSlots());
        dupe.removeAll(operationHistory.keySet());
        dupe = dupe.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        return dupe;
    }

    /**
     * @return The fuel button.
     */
    @NotNull
    public T getFuelButton() {
        return this.fuelButton;
    }

    /**
     * @return The input button.
     */
    @NotNull
    public T getInputButton() {
        return this.inputButton;
    }

    /**
     * @return The output button.
     */
    @NotNull
    public T getOutputButton() {
        return this.outputButton;
    }

    /**
     * Will apply the experience to the player and remove the operation from the history.
     *
     * @param player The player to apply the experience to.
     * @param slot   The slot of the operation.
     */
    public MultiSuperFurnace<T> apply(Player player, int slot) {
        FurnaceOperation operation = operationHistory.get(slot);
        if (operation == null)
            return this;
        float experience = operation.experience();
        Experience.changeExp(player, experience);
        operationHistory.remove(slot);
        return this;
    }
}