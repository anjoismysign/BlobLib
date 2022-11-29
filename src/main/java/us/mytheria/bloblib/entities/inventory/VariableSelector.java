package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;
import us.mytheria.bloblib.managers.FileManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class VariableSelector<T> extends BlobInventory {
    private final String dataType;
    private final HashMap<Integer, T> values;
    private final UUID builderId;
    private final VariableFiller<T> filler;
    private int page;
    private final int itemsPerPage;

    public static BlobInventory DEFAULT() {
        FileManager fileManager = BlobLib.getInstance().getFileManager();
        YamlConfiguration inventories = fileManager.getYml(fileManager.inventoriesFile());
        return fromConfigurationSection(inventories.getConfigurationSection("VariableSelector"));
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

    public T getValue(int slot) {
        return values.get(slot);
    }

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

    public UUID getBuilderId() {
        return builderId;
    }

    public boolean isPreviousPageButton(int slot) {
        return getSlots("Previous-Page").contains(slot);
    }

    public boolean isNextPageButton(int slot) {
        return getSlots("Next-Page").contains(slot);
    }

    public String getDataType() {
        return dataType;
    }

    public HashMap<Integer, T> getValues() {
        return values;
    }

    public void addValue(int slot, T value) {
        values.put(slot, value);
    }

    public VariableFiller<T> getFiller() {
        return filler;
    }

    public int valuesSize() {
        return getValues().size();
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setPage(int page) {
        if (page < 1)
            return;
        if (page > getTotalPages()) {
            return;
        }
        this.page = page;
        loadPage(page);
    }

    public void nextPage() {
        setPage(page + 1);
        Player player = getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    public void previousPage() {
        setPage(page - 1);
        Player player = getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    public int getTotalPages() {
        return filler.totalPages(itemsPerPage);
    }
}
