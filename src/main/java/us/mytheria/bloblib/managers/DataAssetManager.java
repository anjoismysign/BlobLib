package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.DataAsset;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class DataAssetManager<T extends DataAsset> {
    private final File assetDirectory;
    private final BiFunction<ConfigurationSection, String, T> readFunction;
    private final DataAssetType type;
    private final Predicate<ConfigurationSection> filter;

    private final BlobLib main;
    private HashMap<String, Set<String>> pluginAssets;
    private HashMap<String, Integer> duplicates;
    private HashMap<String, T> assets;

    /**
     * Creates a new instance of the DataAssetManager
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset.
     *                       Think of it as checks that once met, the ConfigurationSection
     *                       is considered from an asset.
     * @param <T>            The type of the asset
     * @return The new instance of the DataAssetManager
     */
    public static <T extends DataAsset> DataAssetManager<T> of(@NotNull File assetDirectory,
                                                               @NotNull BiFunction<ConfigurationSection, String, T> readFunction,
                                                               @NotNull DataAssetType type,
                                                               @NotNull Predicate<ConfigurationSection> filter) {
        Objects.requireNonNull(assetDirectory, "Asset directory cannot be null");
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        Objects.requireNonNull(type, "Data asset type cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        if (!assetDirectory.isDirectory())
            throw new IllegalArgumentException("File '" + assetDirectory.getPath() + "' is not a directory");
        return new DataAssetManager<>(assetDirectory,
                readFunction, type, filter);
    }

    private DataAssetManager(@NotNull File assetDirectory,
                             @NotNull BiFunction<ConfigurationSection, String, T> readFunction,
                             @NotNull DataAssetType type,
                             Predicate<ConfigurationSection> filter) {
        this.main = BlobLib.getInstance();
        this.assetDirectory = assetDirectory;
        this.readFunction = readFunction;
        this.type = type;
        this.filter = filter;
    }

    public void reload() {
        assets = new HashMap<>();
        pluginAssets = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(assetDirectory);
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + key + "' (found " + value + " instances)"));
    }

    public void reload(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginAssets.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginAssets.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().getDirectory(type);
        if (directory == null)
            throw new NullPointerException("Directory for " + type.name() + " is null");
        loadFiles(directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        this.pluginAssets.remove(pluginName);
    }

    private void loadFiles(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                try {
                    loadYamlConfiguration(file);
                } catch (ConfigurationFieldException exception) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadFiles(file);
        }
    }

    private void loadYamlConfiguration(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (filter.test(yamlConfiguration)) {
            try {
                T asset = readFunction.apply(yamlConfiguration, fileName);
                if (asset == null)
                    return;
                addOrCreate(asset, fileName);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!filter.test(section))
                return;
            try {
                T asset = readFunction.apply(section, reference);
                addOrCreate(asset, reference);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (filter.test(yamlConfiguration)) {
            try {
                T asset = readFunction.apply(yamlConfiguration, fileName);
                if (asset == null)
                    return;
                addOrCreate(asset, fileName);
                pluginAssets.get(plugin.getName()).add(fileName);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!filter.test(section))
                return;
            try {
                T asset = readFunction.apply(section, reference);
                addOrCreate(asset, reference);
                pluginAssets.get(plugin.getName()).add(reference);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
        });
    }

    private boolean addOrCreate(T asset, String reference) {
        if (assets.containsKey(reference)) {
            addDuplicate(reference);
            return false;
        }
        assets.put(reference, asset);
        return true;
    }

    public void continueLoadingAssets(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        duplicates.clear();
        for (File file : files)
            loadYamlConfiguration(file, plugin);
        if (warnDuplicates)
            duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate " + type.name() + ": '" + key + "' (found " + value + " instances)"));
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    @Nullable
    public T getAsset(@NotNull String key) {
        Objects.requireNonNull(key);
        return assets.get(key);
    }

    @NotNull
    public List<T> getAssets() {
        return assets.values().stream().toList();
    }
}
