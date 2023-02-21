package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.BlobInventory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InventoryManager {
    private final BlobLib main;
    private HashMap<String, BlobInventory> inventories;
    private HashMap<String, Set<String>> pluginInventories;
    private HashMap<String, Integer> duplicates;

    public InventoryManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        inventories = new HashMap<>();
        pluginInventories = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().inventoriesDirectory());
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, ManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginInventories.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginInventories.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().inventoriesDirectory();
        loadFiles(plugin, directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void loadBlobPlugin(BlobPlugin plugin, ManagerDirector director) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        if (!pluginInventories.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has not been loaded");
        pluginInventories.get(pluginName).forEach(inventories::remove);
        pluginInventories.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.unload(plugin);
    }

    private void loadFiles(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                loadYamlConfiguration(file);
            }
            if (file.isDirectory())
                loadFiles(path);
        }
    }

    private void loadFiles(BlobPlugin plugin, File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                loadYamlConfiguration(plugin, file);
            }
            if (file.isDirectory())
                loadFiles(plugin, path);
        }
    }

    private void loadYamlConfiguration(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            if (inventories.containsKey(fileName)) {
                addDuplicate(fileName);
                return;
            }
            add(fileName, BlobInventory.fromConfigurationSection(yamlConfiguration));
            pluginInventories.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (inventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            add(reference, BlobInventory.fromConfigurationSection(section));
            pluginInventories.get(plugin.getName()).add(reference);
        });
    }

    public static void continueLoading(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadYamlConfiguration(plugin, file);
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void continueLoading(BlobPlugin plugin, File... files) {
        continueLoading(plugin, true, files);
    }

    /**
     * @param plugin The plugin that is loading the inventory
     * @param file   The file to load
     * @deprecated Use {@link #continueLoading(BlobPlugin, File...)} instead
     */
    @Deprecated
    public static void loadAndRegisterYamlConfiguration(BlobPlugin plugin, File file) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.loadYamlConfiguration(plugin, file);
        manager.duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    private void loadYamlConfiguration(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            add(fileName, BlobInventory.fromConfigurationSection(yamlConfiguration));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (inventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            add(reference, BlobInventory.fromConfigurationSection(section));
        });
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    @Nullable
    public BlobInventory getInventory(String key) {
        return inventories.get(key);
    }

    @Nullable
    public BlobInventory cloneInventory(String key) {
        BlobInventory inventory = inventories.get(key);
        if (inventory == null)
            return null;
        return inventory.copy();
    }

    private void add(String key, BlobInventory inventory) {
        inventories.put(key, inventory);
    }
}
