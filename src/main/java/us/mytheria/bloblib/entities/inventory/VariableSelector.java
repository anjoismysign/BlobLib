package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;
import us.mytheria.bloblib.managers.FileManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class VariableSelector<T> extends BlobInventory {
    private final String dataType;
    private final HashMap<Integer, T> values;
    private final UUID builderId;
    private final VariableFiller<T> filler;
    private int page;
    private final int itemsPerPage;

    public static BlobInventory DEFAULT() {
        FileManager fileManager = BlobLib.getInstance().getFileManager();
        YamlConfiguration inventories = fileManager.getYml(fileManager.defaultInventoriesFile());
        return fromConfigurationSection(inventories.getConfigurationSection("VariableSelector"));
    }

    public static BlobInventory DEFAULT_ITEMSTACKREADER() {
        FileManager fileManager = BlobLib.getInstance().getFileManager();
        YamlConfiguration inventories = fileManager.getYml(fileManager.defaultInventoriesFile());
        return smartFromConfigurationSection(inventories.getConfigurationSection("VariableSelector"));
    }

    public VariableSelector(BlobInventory blobInventory, UUID builderId,
                            String dataType, VariableFiller<T> filler) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.filler = filler;
        this.builderId = builderId;
        this.values = new HashMap<>();
        this.dataType = dataType;
        setTitle(blobInventory.getTitle().replace("%variable%", dataType));
        buildInventory();
        this.page = 1;
        this.itemsPerPage = getSlots("White-Background").size();
        loadInConstructor();
    }

    public void loadInConstructor() {
        loadPage(page, false);
    }

    public void loadPage(int page, boolean refill) {
        if (page < 1)
            return;
        if (getTotalPages() < page) {
            return;
        }
        if (refill)
            refillButton("White-Background");
        values.clear();
        List<VariableValue<T>> values = filler.page(page, itemsPerPage);
        for (int i = 0; i < values.size(); i++) {
            setValue(i, values.get(i));
        }
    }

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

    public void loadPage(int page) {
        loadPage(page, true);
    }

    public void loadPageAndOpen(int page) {
        loadPage(page, true);
        open();
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public void setValue(int slot, VariableValue<T> value) {
        values.put(slot, value.value());
        setButton(slot, value.itemStack());
    }

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
     * @return true if the slot is a previous page button, false otherwise
     */
    public boolean isPreviousPageButton(int slot) {
        return getSlots("Previous-Page").contains(slot);
    }

    /**
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
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    /**
     * goes to previous page
     */
    public void previousPage() {
        setPage(page - 1);
        Player player = getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
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
}
