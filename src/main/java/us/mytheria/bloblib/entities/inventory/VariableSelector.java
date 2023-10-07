package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

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
    private int page;
    private int itemsPerPage;

    /**
     * Creates a new VariableSelector
     *
     * @param player the player to get Locale from
     * @return the new VariableSelector
     */
    public static BlobInventory DEFAULT(@NotNull Player player) {
        return BlobLibInventoryAPI.getInstance().getBlobInventory("VariableSelector", player);
    }

    /**
     * Creates a new VariableSelector.
     * Will use the default Locale
     *
     * @return the new VariableSelector
     */
    public static BlobInventory DEFAULT() {
        return BlobLibInventoryAPI.getInstance().getBlobInventory("VariableSelector");
    }

    /**
     * creates a new VariableSelector
     *
     * @param blobInventory the inventory to use
     * @param builderId     the id of the builder
     * @param dataType      the data type
     * @param filler        the filler to use
     */
    public VariableSelector(BlobInventory blobInventory, UUID builderId,
                            String dataType, VariableFiller<T> filler) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.filler = filler;
        this.builderId = builderId;
        this.values = new HashMap<>();
        this.dataType = dataType.toUpperCase();
        if (dataType != null)
            setTitle(blobInventory.getTitle().replace("%variable%", dataType));
        buildInventory();
        this.page = 1;
        this.itemsPerPage = 1;
        Set<Integer> slots = getSlots("White-Background");
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
            refillButton("White-Background");
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
            refillButton("White-Background");
        values.clear();
        List<VariableValue<T>> values = filler.customPage(page, itemsPerPage, list, function);
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

    /**
     * loads the specified page
     *
     * @param page the page to load
     */
    public void loadPage(int page) {
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
     * @param slot the slot to check
     * @return true if the slot is a next page button, false otherwise
     */
    public boolean isNextPageButton(int slot) {
        return getSlots("Next-Page").contains(slot);
    }

    /**
     * @return the datatype that was specified in the constructor
     */
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
     * goes to next page
     */
    public void nextPage() {
        setPage(page + 1);
        Player player = getPlayer();
        BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click").handle(player);
    }

    /**
     * goes to previous page
     */
    public void previousPage() {
        setPage(page - 1);
        Player player = getPlayer();
        BlobLibSoundAPI.getInstance().getSound("Builder.Button-Click").handle(player);
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

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }
}
