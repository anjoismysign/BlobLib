package io.github.anjoismysign.bloblib.entities.inventory;

import io.github.anjoismysign.bloblib.entities.PlayerAddress;
import io.github.anjoismysign.bloblib.entities.VariableFiller;
import io.github.anjoismysign.bloblib.entities.VariableValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <T> type of the variable
 * @author anjoismysign
 * <p>
 * A VariableSelector is a BlobInventory that can be used to select a variable.
 * An example of the usage would be a selector for a collection so each element
 * would be displayed as an ItemStack inside the GUI since VariableSelector
 * extends BlobInventory.
 */
public abstract class VariableSelector<T> extends BlobInventory {
    private final String dataType;
    private final HashMap<Integer, T> values;
    private final UUID builderId;
    private final VariableFiller<T> filler;
    private final Consumer<Player> returnAction;
    @Nullable
    private Function<T, ItemStack> loadFunction;
    @Nullable
    private Supplier<Collection<T>> collectionSupplier;
    @Nullable
    private String buttonRangeKey;
    private int page;
    private int itemsPerPage;

    /**
     * Creates a new VariableSelector
     *
     * @param player the player to get Locale from
     * @return the new VariableSelector
     */
    public static BlobInventory DEFAULT(@NotNull Player player) {
        return BlobInventory
                .ofKeyAddressOrThrow("VariableSelector", PlayerAddress.builder()
                        .setPlayer(player).build());
    }

    /**
     * Creates a new VariableSelector.
     * Will use the default Locale
     *
     * @return the new VariableSelector
     */
    public static BlobInventory DEFAULT() {
        return BlobInventory.ofKeyOrThrow("VariableSelector", null);
    }

    /**
     * creates a new VariableSelector
     *
     * @param blobInventory the inventory to use
     * @param builderId     the id of the builder
     * @param dataType      the data type
     * @param filler        the filler to use
     * @param returnAction  the action to run when the return button is clicked
     */
    public VariableSelector(BlobInventory blobInventory, UUID builderId,
                            String dataType, VariableFiller<T> filler,
                            @Nullable Consumer<Player> returnAction) {
        this(blobInventory, builderId, dataType, filler, returnAction, null, null, null);
    }

    /**
     * creates a new VariableSelector
     *
     * @param blobInventory      the inventory to use
     * @param builderId          the id of the builder
     * @param dataType           the data type
     * @param filler             the filler to use
     * @param returnAction       the action to run when the return button is clicked
     * @param loadFunction       the function to use to convert the list to an itemstack
     * @param collectionSupplier the collection supplier
     * @param buttonRangeKey     the button range key
     */
    public VariableSelector(BlobInventory blobInventory,
                            UUID builderId,
                            @Nullable String dataType,
                            VariableFiller<T> filler,
                            @Nullable Consumer<Player> returnAction,
                            @Nullable Function<T, ItemStack> loadFunction,
                            @Nullable Supplier<Collection<T>> collectionSupplier,
                            @Nullable String buttonRangeKey) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager(), blobInventory.getKey(), blobInventory.getLocale(), blobInventory.getPath());
        this.returnAction = returnAction == null ? HumanEntity::closeInventory : returnAction;
        this.loadFunction = loadFunction;
        this.collectionSupplier = collectionSupplier;
        this.buttonRangeKey = buttonRangeKey;
        this.filler = filler;
        this.builderId = builderId;
        this.values = new HashMap<>();
        this.dataType = dataType;
//        if (dataType != null)
//            setTitle(blobInventory.getTitle().replace("%variable%", dataType));
        this.page = 1;
        this.itemsPerPage = 1;
        Set<Integer> slots = getSlots(getButtonRangeKey());
        if (slots != null)
            setItemsPerPage(slots.size());
        loadFirstPage();
    }

    /**
     * Loads the first page
     */
    public void loadFirstPage() {
        loadPage(page, false);
    }

    /**
     * loads the specified page with optional white background refill
     *
     * @param page                  the page to load
     * @param whiteBackgroundRefill whether to refill the white background
     */
    public void loadPage(int page, boolean whiteBackgroundRefill) {
        if (page < 1)
            return;
        if (getTotalPages() < page) {
            return;
        }
        if (whiteBackgroundRefill)
            refillButton(getButtonRangeKey());
        values.clear();
        List<VariableValue<T>> values = filler.page(page, itemsPerPage);
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

    /**
     * loads the specified page using a specific function
     *
     * @param page     the page to load
     * @param refill   whether to refill the background
     * @param list     the list to load the page from
     * @param function the function to use to convert the list to an itemstack
     */
    public void loadCustomPage(int page, boolean refill, List<T> list, Function<T, ItemStack> function) {
        if (page < 1)
            return;
        if (getTotalPages() < page) {
            return;
        }
        if (refill)
            refillButton(getButtonRangeKey());
        values.clear();
        List<VariableValue<T>> values = filler.customPage(page, itemsPerPage, list, function);
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

    /**
     * loads the page with the given page number
     *
     * @param page     the page number
     * @param refill   if the background should be refilled
     * @param function the function to apply
     */
    public void loadCustomPage(int page, boolean refill, Function<T, ItemStack> function) {
        if (page < 1) {
            return;
        }
        if (getTotalPages() < page) {
            return;
        }
        if (refill)
            refillButton(getButtonRangeKey());
        clearValues();
        InventoryButton inventoryButton = Objects.requireNonNull(getButton(buttonRangeKey), "buttonRangeKey is not an InventoryButton");
        List<Integer> slots = inventoryButton.getSlots().stream()
                .sorted(Comparator.comparingInt(Integer::intValue))
                .toList();
        List<VariableValue<T>> values = this.customPage(page, getItemsPerPage(), function);
        Iterator<Integer> iterator = slots.iterator();
        for (VariableValue<T> value : values) {
            if (!iterator.hasNext())
                break;
            int next = iterator.next();
            setValue(next, value);
        }
    }

    /**
     * loads the specified page
     *
     * @param page the page to load
     */
    public void loadPage(int page) {
        if (loadFunction != null && collectionSupplier != null)
            loadCustomPage(page, true, loadFunction);
        else
            loadPage(page, true);
    }

    /**
     * loads the specified page and opens the inventory
     *
     * @param page the page to load
     */
    public void loadPageAndOpen(int page) {
        loadPage(page, true);
        open();
    }

    /**
     * opens the inventory for the player
     */
    public void open() {
        getPlayer().openInventory(getInventory());
    }

    /**
     * sets a value to a slot
     *
     * @param slot  the slot to set the value to
     * @param value the value to set
     */
    public void setValue(int slot, VariableValue<T> value) {
        values.put(slot, value.value());
        setButton(slot, value.itemStack());
    }

    /**
     * clears all values and items from the hashmap
     */
    public void clearValues() {
        values.clear();
    }

    /**
     * @param slot the slot that was clicked
     * @return the value that was stored in the slot
     */
    @Nullable
    public T getValue(int slot) {
        return values.get(slot);
    }

    /**
     * @return the page that is currently loaded
     */
    public int getPage() {
        return page;
    }

    /**
     * goes/opens to specific page
     *
     * @param page the page to go to
     */
    public void setPage(int page) {
        if (page < 1)
            return;
        if (page > getTotalPages()) {
            return;
        }
        this.page = page;
        loadPage(page);
    }

    /**
     * @return player matching builderId.
     * null if no player is found.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(builderId);
    }

    /**
     * @return the id of the player that opened the inventory
     */
    public UUID getBuilderId() {
        return builderId;
    }

    /**
     * @param slot the slot to check
     * @return true if the slot is a previous page button, false otherwise
     */
    public boolean isPreviousPageButton(int slot) {
        return getSlots("Previous-Page").contains(slot);
    }

    /**
     * Checks if the slot is a return button
     *
     * @param slot the slot to check
     * @return true if the slot is a return button, false otherwise
     */
    public boolean isReturnButton(int slot) {
        Set<Integer> slots = getSlots("Return");
        if (slots == null)
            return false;
        return slots.contains(slot);
    }

    /**
     * @param slot the slot to check
     * @return true if the slot is a next page button, false otherwise
     */
    public boolean isNextPageButton(int slot) {
        return getSlots("Next-Page").contains(slot);
    }

    /**
     * @return the datatype that was specified in the constructor
     */
    @Nullable
    public String getDataType() {
        return dataType;
    }

    /**
     * @return values, in this case a HashMap
     */
    public HashMap<Integer, T> getValues() {
        return values;
    }

    /**
     * adds a value to the values map
     *
     * @param slot  the slot
     * @param value the value
     */
    public void addValue(int slot, T value) {
        values.put(slot, value);
    }

    /**
     * @return filler
     */
    public VariableFiller<T> getFiller() {
        return filler;
    }

    /**
     * @return size of values / total values
     */
    public int valuesSize() {
        return getValues().size();
    }

    /**
     * @return total items per page
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     * Goes to next page
     */
    public void nextPage() {
        setPage(page + 1);
    }

    /**
     * Goes to previous page
     */
    public void previousPage() {
        setPage(page - 1);
    }

    /**
     * Will process the return action
     */
    public void processReturn() {
        Player player = getPlayer();
        returnAction.accept(player);
    }

    /**
     * @return total pages.
     */
    public int getTotalPages() {
        return filler.totalPages(itemsPerPage);
    }

    /**
     * adds values to the values map.
     *
     * @param collection   values to add.
     * @param noDuplicates if true, will not add duplicates.
     */
    public void addValues(Collection<T> collection, boolean noDuplicates) {
        for (T t : collection) {
            if (noDuplicates && values.containsValue(t))
                continue;
            addValue(valuesSize(), t);
        }
    }

    /**
     * adds values to the values map.
     *
     * @param collection values to add.
     */
    public void addValues(Collection<T> collection) {
        addValues(collection, false);
    }

    /**
     * @return the list
     */
    public List<T> getList() {
        if (collectionSupplier == null)
            return new ArrayList<>();
        return collectionSupplier.get().stream().toList();
    }

    /**
     * returns specific page with provided function without loading
     *
     * @param page         the page
     * @param itemsPerPage the items per page
     * @param function     the function to apply
     * @return the list of values
     */
    public List<VariableValue<T>> customPage(int page, int itemsPerPage, Function<T, ItemStack> function) {
        int start = (page - 1) * itemsPerPage;
        int end = start + (itemsPerPage);
        ArrayList<VariableValue<T>> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            T get;
            try {
                get = getList().get(i);
                ItemStack itemStack = function.apply(get);
                if (itemStack == null)
                    continue;
                values.add(new VariableValue<>(itemStack, get));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return values;
    }

    public void setLoadFunction(@NotNull Function<T, ItemStack> loadFunction) {
        Objects.requireNonNull(loadFunction, "loadFunction cannot be null");
        this.loadFunction = loadFunction;
    }

    public void setCollectionSupplier(@NotNull Supplier<Collection<T>> collectionSupplier) {
        Objects.requireNonNull(collectionSupplier, "listSupplier cannot be null");
        this.collectionSupplier = collectionSupplier;
    }

    @NotNull
    private String getButtonRangeKey() {
        return buttonRangeKey == null ? "White-Background" : buttonRangeKey;
    }

    public void setButtonRangeKey(@NotNull String buttonRangeKey) {
        Objects.requireNonNull(buttonRangeKey, "whiteBackgroundName cannot be null");
        this.buttonRangeKey = buttonRangeKey;
    }
}
