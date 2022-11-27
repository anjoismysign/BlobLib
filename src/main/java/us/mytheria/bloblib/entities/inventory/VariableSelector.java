package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.VariableFiller;
import us.mytheria.bloblib.entities.VariableValue;
import us.mytheria.bloblib.managers.FileManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public abstract class VariableSelector extends BlobInventory {
    private String dataType;
    private HashMap<Integer, Object> values;
    private final UUID builderId;
    private VariableFiller filler;
    private int page;

    public static BlobInventory DEFAULT() {
        FileManager fileManager = BlobLib.getInstance().getFileManager();
        YamlConfiguration inventories = fileManager.getYml(fileManager.inventoriesFile());
        return fromConfigurationSection(inventories.getConfigurationSection("VariableSelector"));
    }

    public VariableSelector(BlobInventory blobInventory, UUID builderId,
                            String dataType, VariableFiller filler) {
        super(blobInventory.getTitle(), blobInventory.getSize(), blobInventory.getButtonManager());
        this.filler = filler;
        this.builderId = builderId;
        this.values = new HashMap<>();
        this.dataType = dataType;
        setTitle(blobInventory.getTitle().replace("%variable%", dataType));
        buildInventory();
        this.page = 1;
        loadPage(page, false);
    }

    @Override
    public void loadDefaultButtons() {
        setDefaultButtons(new HashMap<>());
    }

    public void loadPage(int page, boolean refill) {
        int itemsPerPage = getSlots("White-Background").size();
        int totalPages = filler.totalPages(itemsPerPage);
        if (totalPages < page) {
            return;
        }
        if (refill)
            refillButton("White-Background");
        values.clear();
        VariableValue[] values = filler.page(page, itemsPerPage);
        for (int i = 0; i < values.length; i++) {
            setValue(i, values[i]);
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

    public void setValue(int slot, VariableValue value) {
        values.put(slot, value.value());
        setButton(slot, value.itemStack());
    }

    public Object getValue(int slot) {
        return values.get(slot);
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

    public HashMap<Integer, Object> getValues() {
        return values;
    }

    public void addValue(int slot, Object value) {
        values.put(slot, value);
    }

    public VariableFiller getFiller() {
        return filler;
    }

    public int valuesSize() {
        return getValues().size();
    }

    public void setPage(int page) {
        int total = filler.totalPages(getSlots("White-Background").size());
        if (page > total) {
            return;
        }
        this.page = page;
        loadPage(page);
    }

    public void nextPage() {
        setPage(page + 1);
    }

    public void previousPage() {
        setPage(page - 1);
    }
}
