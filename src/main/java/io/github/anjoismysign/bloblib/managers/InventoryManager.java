package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.IFileManager;
import io.github.anjoismysign.bloblib.entities.inventory.BlobInventory;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryBuilderCarrier;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryButton;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryDataRegistry;
import io.github.anjoismysign.bloblib.entities.inventory.MetaBlobInventory;
import io.github.anjoismysign.bloblib.entities.inventory.MetaInventoryButton;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static io.github.anjoismysign.bloblib.entities.inventory.InventoryBuilderCarrier.BLOB_FROM_CONFIGURATION_SECTION;

public class InventoryManager {
    private final BlobLib main;
    private HashMap<String, InventoryDataRegistry<InventoryButton>> blobInventories;
    private HashMap<String, InventoryDataRegistry<MetaInventoryButton>> metaInventories;
    private HashMap<String, MetaInventoryShard> metaInventoriesShards;
    private HashMap<String, Set<String>> pluginBlobInventories;
    private HashMap<String, Set<String>> pluginMetaInventories;
    private HashMap<String, Integer> duplicates;

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.unload(plugin);
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
        loadBlobInventories(main.getFileManager().getDirectory(DataAssetType.BLOB_INVENTORY));
        loadMetaInventories(main.getFileManager().getDirectory(DataAssetType.META_BLOB_INVENTORY));
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate Inventory: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginBlobInventories.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginBlobInventories.put(pluginName, new HashSet<>());
        pluginMetaInventories.put(pluginName, new HashSet<>());
        duplicates.clear();
        IFileManager fileManager = director.getFileManager();
        File blobDirectory = fileManager.getDirectory(DataAssetType.BLOB_INVENTORY);
        loadBlobInventories(blobDirectory);
        File metaDirectory = fileManager.getDirectory(DataAssetType.META_BLOB_INVENTORY);
        loadMetaInventories(metaDirectory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate Inventory: '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        Set<String> blobInventories = pluginBlobInventories.get(pluginName);
        if (blobInventories == null)
            return;
        Iterator<String> iterator = blobInventories.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.blobInventories.remove(inventoryName);
            iterator.remove();
        }
        Set<String> metaInventoryKeys = pluginMetaInventories.get(pluginName);
        iterator = metaInventoryKeys.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.metaInventories.remove(inventoryName);
            iterator.remove();
        }
        pluginBlobInventories.remove(pluginName);
        pluginMetaInventories.remove(pluginName);
    }

    private void loadBlobInventories(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadBlobInventory(file);
                } catch (ConfigurationFieldException exception) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadBlobInventories(file);
        }
    }

    private void loadMetaInventories(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadMetaInventory(file);
                } catch (ConfigurationFieldException exception) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadMetaInventories(file);
        }
    }

    private void loadBlobInventory(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String path = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addBlobInventory(fileName, BLOB_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName, path));
            pluginBlobInventories.get(plugin.getName()).add(fileName);
            return;
        }
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            InventoryBuilderCarrier<InventoryButton> carrier = BLOB_FROM_CONFIGURATION_SECTION(section, reference, path);
            carrier = carrier.setLocale(locale);
            addBlobInventory(reference, carrier);
            pluginBlobInventories.get(plugin.getName()).add(reference);
        });
    }

    private void loadMetaInventory(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String path = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addMetaInventory(fileName, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName, path));
            pluginMetaInventories.get(plugin.getName()).add(fileName);
            return;
        }
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            InventoryBuilderCarrier<MetaInventoryButton> carrier = InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(section, reference, path);
            carrier = carrier.setLocale(locale);
            addMetaInventory(reference, carrier);
            pluginMetaInventories.get(plugin.getName()).add(reference);
        });
    }

    private void loadBlobInventory(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String path = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            InventoryBuilderCarrier<InventoryButton> carrier = BLOB_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName, path);
            addBlobInventory(fileName, carrier);
            return;
        }
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            InventoryBuilderCarrier<InventoryButton> carrier = BLOB_FROM_CONFIGURATION_SECTION(section, reference, path);
            carrier = carrier.setLocale(locale);
            addBlobInventory(reference, carrier);
        });
    }

    private void loadMetaInventory(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String path = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addMetaInventory(fileName, InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(yamlConfiguration, fileName, path));
            return;
        }
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            InventoryBuilderCarrier<MetaInventoryButton> carrier = InventoryBuilderCarrier.
                    META_FROM_CONFIGURATION_SECTION(section, reference, path);
            carrier = carrier.setLocale(locale);
            addMetaInventory(reference, carrier);
        });
    }


    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    /**
     * @return An unmodifiable map of all blob inventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<InventoryButton>> getBlobInventories() {
        return Collections.unmodifiableMap(blobInventories);
    }

    /**
     * @return An unmodifiable map of all meta inventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<MetaInventoryButton>> getMetaInventories() {
        return Collections.unmodifiableMap(metaInventories);
    }

    @Nullable
    public InventoryDataRegistry<InventoryButton> getInventoryDataRegistry(String key) {
        return blobInventories.get(key);
    }

    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key, String locale) {
        InventoryDataRegistry<InventoryButton> registry = getInventoryDataRegistry(key);
        if (registry == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        return registry.get(locale);
    }

    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key) {
        InventoryDataRegistry<InventoryButton> registry = getInventoryDataRegistry(key);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    @Nullable
    public BlobInventory cloneInventory(String key, String locale) {
        BlobInventory inventory = BlobInventory.ofKeyOrThrow(key, locale);
        return inventory.copy();
    }

    @Nullable
    public BlobInventory cloneInventory(String key) {
        return cloneInventory(key, null);
    }

    @Nullable
    public InventoryDataRegistry<MetaInventoryButton> getMetaInventoryDataRegistry(String key) {
        return metaInventories.get(key);
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key, String locale) {
        InventoryDataRegistry<MetaInventoryButton> registry = getMetaInventoryDataRegistry(key);
        if (registry == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        return registry.get(locale);
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        InventoryDataRegistry<MetaInventoryButton> registry = getMetaInventoryDataRegistry(key);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key, String locale) {
        InventoryBuilderCarrier<MetaInventoryButton> carrier = getMetaInventoryBuilderCarrier(key, locale);
        if (carrier == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key) {
        InventoryDataRegistry<MetaInventoryButton> registry = getMetaInventoryDataRegistry(key);
        if (registry == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(registry.getDefault());
    }

    @Nullable
    public MetaBlobInventory cloneMetaInventory(String key, String locale) {
        MetaBlobInventory inventory = getMetaInventory(key, locale);
        if (inventory == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        return inventory.copy();
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
        InventoryDataRegistry<InventoryButton> dataRegistry = blobInventories.get(key);
        if (dataRegistry == null)
            dataRegistry = InventoryDataRegistry.of("en_us", key);
        if (!dataRegistry.process(inventory)) {
            addDuplicate(key);
            return;
        }
        blobInventories.put(key, dataRegistry);
    }

    private void addMetaInventory(String key, InventoryBuilderCarrier<MetaInventoryButton> inventory) {
        InventoryDataRegistry<MetaInventoryButton> dataRegistry = metaInventories.get(key);
        if (dataRegistry == null)
            dataRegistry = InventoryDataRegistry.of("en_us", key);
        if (!dataRegistry.process(inventory)) {
            addDuplicate(key);
            return;
        }
        metaInventories.put(key, dataRegistry);
        metaInventoriesShards.computeIfAbsent(inventory.type(), type -> new MetaInventoryShard())
                .addInventory(inventory, key);
    }
}
