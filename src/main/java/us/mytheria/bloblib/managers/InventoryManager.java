package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobFileManager;
import us.mytheria.bloblib.entities.inventory.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InventoryManager {
    private final BlobLib main;
    private HashMap<String, InventoryBuilderCarrier<InventoryButton>> blobInventories;
    private HashMap<String, InventoryBuilderCarrier<MetaInventoryButton>> metaInventories;
    private HashMap<String, MetaInventoryShard> metaInventoriesShards;
    private HashMap<String, Set<String>> pluginBlobInventories;
    private HashMap<String, Set<String>> pluginMetaInventories;
    private HashMap<String, Integer> duplicates;

    public InventoryManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        metaInventoriesShards = new HashMap<>();
        blobInventories = new HashMap<>();
        metaInventories = new HashMap<>();
        pluginBlobInventories = new HashMap<>();
        pluginMetaInventories = new HashMap<>();
        duplicates = new HashMap<>();
        loadBlobInventories(main.getFileManager().inventoriesDirectory());
        loadMetaInventories(main.getFileManager().metaInventoriesDirectory());
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate Inventory: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, ManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginBlobInventories.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginBlobInventories.put(pluginName, new HashSet<>());
        pluginMetaInventories.put(pluginName, new HashSet<>());
        duplicates.clear();
        BlobFileManager fileManager = director.getFileManager();
        File blobDirectory = fileManager.inventoriesDirectory();
        loadBlobInventories(plugin, blobDirectory);
        File metaDirectory = fileManager.metaInventoriesDirectory();
        loadMetaInventories(plugin, metaDirectory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate Inventory: '" + key + "' (found " + value + " instances)"));
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
        if (!pluginBlobInventories.containsKey(pluginName))
            return;
        pluginBlobInventories.get(pluginName).forEach(blobInventories::remove);
        pluginMetaInventories.get(pluginName).forEach(metaInventories::remove);
        pluginBlobInventories.remove(pluginName);
        pluginMetaInventories.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.unload(plugin);
    }

    private void loadBlobInventories(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                loadBlobInventory(file);
            }
            if (file.isDirectory())
                loadBlobInventories(path);
        }
    }

    private void loadMetaInventories(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                loadMetaInventory(file);
            }
            if (file.isDirectory())
                loadMetaInventories(path);
        }
    }

    private void loadBlobInventories(BlobPlugin plugin, File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                loadBlobInventory(plugin, file);
            }
            if (file.isDirectory())
                loadBlobInventories(plugin, path);
        }
    }

    private void loadMetaInventories(BlobPlugin plugin, File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                loadMetaInventory(plugin, file);
            }
            if (file.isDirectory())
                loadMetaInventory(plugin, path);
        }
    }

    private void loadBlobInventory(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            if (blobInventories.containsKey(fileName)) {
                addDuplicate(fileName);
                return;
            }
            addBlobInventory(fileName, InventoryBuilderCarrier.
                    BLOB_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName));
            pluginBlobInventories.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (blobInventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            addBlobInventory(reference, InventoryBuilderCarrier.
                    BLOB_FROM_CONFIGURATION_SECTION(section, reference));
            pluginBlobInventories.get(plugin.getName()).add(reference);
        });
    }

    private void loadMetaInventory(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            if (metaInventories.containsKey(fileName)) {
                addDuplicate(fileName);
                return;
            }
            addMetaInventory(fileName, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName));
            pluginMetaInventories.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (metaInventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            addMetaInventory(reference, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(section, reference));
            pluginMetaInventories.get(plugin.getName()).add(reference);
        });
    }

    public static void continueLoadingBlobInventories(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadBlobInventory(plugin, file);
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void continueLoadingBlobInventories(BlobPlugin plugin, File... files) {
        continueLoadingBlobInventories(plugin, true, files);
    }

    public static void continueLoadingMetaInventories(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadMetaInventory(plugin, file);
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate MetaBlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void continueLoadingMetaInventories(BlobPlugin plugin, File... files) {
        continueLoadingMetaInventories(plugin, true, files);
    }

    private void loadBlobInventory(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addBlobInventory(fileName, InventoryBuilderCarrier.
                    BLOB_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (blobInventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            addBlobInventory(reference, InventoryBuilderCarrier.
                    BLOB_FROM_CONFIGURATION_SECTION(section, reference));
        });
    }

    private void loadMetaInventory(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addMetaInventory(fileName, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            if (blobInventories.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            addMetaInventory(reference, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(section, reference));
        });
    }


    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key) {
        return blobInventories.get(key);
    }

    @Nullable
    public BlobInventory getInventory(String key) {
        InventoryBuilderCarrier<InventoryButton> carrier = getInventoryBuilderCarrier(key);
        if (carrier == null)
            return null;
        return BlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public BlobInventory cloneInventory(String key) {
        BlobInventory inventory = getInventory(key);
        if (inventory == null)
            return null;
        return inventory.copy();
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return metaInventories.get(key);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key) {
        InventoryBuilderCarrier<MetaInventoryButton> carrier = getMetaInventoryBuilderCarrier(key);
        if (carrier == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory cloneMetaInventory(String key) {
        MetaBlobInventory inventory = getMetaInventory(key);
        if (inventory == null)
            return null;
        return inventory.copy();
    }

    @Nullable
    public MetaInventoryShard getMetaInventoryShard(String type) {
        return metaInventoriesShards.get(type);
    }

    private void addBlobInventory(String key, InventoryBuilderCarrier<InventoryButton> inventory) {
        blobInventories.put(key, inventory);
    }

    private void addMetaInventory(String key, InventoryBuilderCarrier<MetaInventoryButton> inventory) {
        metaInventories.put(key, inventory);
        metaInventoriesShards.computeIfAbsent(inventory.type(), type -> new MetaInventoryShard())
                .addInventory(inventory, key);
    }
}
