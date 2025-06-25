package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class DataAssetManager<T extends DataAsset> {
    private final File assetDirectory;
    private final BiFunction<ConfigurationSection, String, T> readFunction;
    private final DataAssetType type;
    private final Predicate<ConfigurationSection> filter;

    private final BlobLib main;
    private final @Nullable BiConsumer<YamlConfiguration, T> saveConsumer;
    private Map<String, Set<String>> pluginAssets;
    private Map<String, Integer> duplicates;
    private Map<String, T> assets;

    /**
     * Creates a new instance of the DataAssetManager
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset.
     *                       Think of it as checks that once met, the ConfigurationSection
     *                       is considered from an asset.
     * @param saveConsumer   Accepts the YamlConfiguration where it is being saved and the asset.
     *                       There's no need to save the file.
     * @param <T>            The type of the asset
     * @return The new instance of the DataAssetManager
     */
    public static <T extends DataAsset> DataAssetManager<T> of(@NotNull File assetDirectory,
                                                               @NotNull BiFunction<ConfigurationSection, String, T> readFunction,
                                                               @NotNull DataAssetType type,
                                                               @NotNull Predicate<ConfigurationSection> filter,
                                                               @Nullable BiConsumer<YamlConfiguration, T> saveConsumer) {
        Objects.requireNonNull(assetDirectory, "Asset directory cannot be null");
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        Objects.requireNonNull(type, "Data asset type cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        if (!assetDirectory.isDirectory())
            throw new IllegalArgumentException("File '" + assetDirectory.getPath() + "' is not a directory");
        return new DataAssetManager<>(assetDirectory,
                readFunction, type, filter, saveConsumer);
    }

    private DataAssetManager(@NotNull File assetDirectory,
                             @NotNull BiFunction<ConfigurationSection, String, T> readFunction,
                             @NotNull DataAssetType type,
                             @NotNull Predicate<ConfigurationSection> filter,
                             @Nullable BiConsumer<YamlConfiguration, T> saveConsumer) {
        this.main = BlobLib.getInstance();
        this.saveConsumer = saveConsumer;
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
        duplicates.forEach((identifier, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + identifier + "' (found " + value + " instances)"));
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
        duplicates.forEach((identifier, value) -> plugin.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + identifier + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        this.pluginAssets.remove(pluginName);
    }

    public void saveAsset(@NotNull File file,
                          @NotNull T asset) {
        if (saveConsumer == null)
            return;
        Objects.requireNonNull(file, "'file' cannot be null");
        Objects.requireNonNull(asset, "'asset' cannot be null");
        if (!file.getName().endsWith(".yml"))
            return;
        File directory = file.getParentFile();
        if (!directory.isDirectory())
            directory.mkdirs();
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
            String identifier = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            saveConsumer.accept(configuration, asset);
            configuration.save(file);
            addOrCreate(asset, identifier);
        } catch (Throwable throwable) {
            main.getLogger().severe(throwable.getMessage() + "\nAt: " + file.getPath());
        }
    }

    private void loadFiles(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
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
            duplicates.forEach((identifier, value) -> plugin.getAnjoLogger()
                    .log("Duplicate " + type.name() + ": '" + identifier + "' (found " + value + " instances)"));
    }

    private void addDuplicate(String identifier) {
        if (duplicates.containsKey(identifier))
            duplicates.put(identifier, duplicates.get(identifier) + 1);
        else
            duplicates.put(identifier, 2);
    }

    @Nullable
    public T getAsset(@NotNull String identifier) {
        Objects.requireNonNull(identifier);
        return assets.get(identifier);
    }

    @NotNull
    public List<T> getAssets() {
        return assets.values().stream().toList();
    }
}
