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
    private HashMap<BlobPlugin, Set<String>> pluginInventories;
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

    public void load(BlobPlugin plugin) {
        if (pluginInventories.containsKey(plugin))
            throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' has already been loaded");
        pluginInventories.put(plugin, new HashSet<>());
        duplicates.clear();
        File directory = plugin.getManagerDirector().getFileManager().inventoriesDirectory();
        loadFiles(plugin, directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.load(plugin);
    }

    public void unload(BlobPlugin plugin) {
        if (!pluginInventories.containsKey(plugin))
            throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' has not been loaded");
        pluginInventories.get(plugin).forEach(inventories::remove);
        pluginInventories.remove(plugin);
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
        if (yamlConfiguration.isInt("Size")) {
            add(fileName, BlobInventory.fromConfigurationSection(yamlConfiguration));
            return;
        }
        yamlConfiguration.getKeys(false).forEach(key -> {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (!section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                if (!subSection.isInt("Size"))
                    return;
                String mapKey = key + "." + subKey;
                if (inventories.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                String reference = key + "." + subKey;
                add(reference, BlobInventory.fromConfigurationSection(subSection));
                pluginInventories.get(plugin).add(reference);
            });
        });
    }

    private void loadYamlConfiguration(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.isInt("Size")) {
            add(fileName, BlobInventory.fromConfigurationSection(yamlConfiguration));
            return;
        }
        yamlConfiguration.getKeys(false).forEach(key -> {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (!section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                if (!subSection.isInt("Size"))
                    return;
                String mapKey = key + "." + subKey;
                if (inventories.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                add(key + "." + subKey, BlobInventory.fromConfigurationSection(subSection));
            });
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
        try {
            return inventory.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void add(String key, BlobInventory inventory) {
        inventories.put(key, inventory);
    }
}
