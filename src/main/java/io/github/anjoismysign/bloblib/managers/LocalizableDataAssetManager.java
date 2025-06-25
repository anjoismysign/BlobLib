package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.Localizable;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.function.TriFunction;
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
import java.util.function.Predicate;

public class LocalizableDataAssetManager<T extends DataAsset & Localizable> {
    private final File assetDirectory;
    private final TriFunction<ConfigurationSection, String, String, T> readFunction;
    private final DataAssetType type;
    private final Predicate<ConfigurationSection> filter;

    private final BlobLib main;
    private final @Nullable BiConsumer<YamlConfiguration, T> saveConsumer;
    private Map<String, Set<String>> assets;
    private Map<String, Integer> duplicates;
    private Map<String, Map<String, T>> locales;

    /**
     * Creates a new instance of the LocalizableDataAssetManager
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset.
     *                       Think of it as checks that once met, the ConfigurationSection
     *                       is considered from an asset.
     * @param saveConsumer   Accepts the YamlConfiguration where it is being saved and the asset.
     *                       There's no need to save the file.
     *                       There's no need to save the locale.
     * @param <T>            The type of the asset
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static <T extends DataAsset & Localizable> LocalizableDataAssetManager<T> of(@NotNull File assetDirectory,
                                                                                        @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                                                                        @NotNull DataAssetType type,
                                                                                        @NotNull Predicate<ConfigurationSection> filter,
                                                                                        @Nullable BiConsumer<YamlConfiguration, T> saveConsumer) {
        Objects.requireNonNull(assetDirectory, "Asset directory cannot be null");
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        Objects.requireNonNull(type, "Data asset type cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        if (!assetDirectory.isDirectory())
            throw new IllegalArgumentException("File '" + assetDirectory.getPath() + "' is not a directory");
        return new LocalizableDataAssetManager<>(assetDirectory,
                readFunction, type, filter, saveConsumer);
    }

    LocalizableDataAssetManager(@NotNull File assetDirectory,
                                @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
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
        locales = new HashMap<>();
        assets = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(assetDirectory);
        duplicates.forEach((identifier, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + identifier + "' (found " + value + " instances)"));
    }

    public void reload(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (assets.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        assets.put(pluginName, new HashSet<>());
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
        this.assets.remove(pluginName);
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
            configuration.set("Locale", asset.locale());
            saveConsumer.accept(configuration, asset);
            configuration.save(file);
            addOrCreateLocale(asset, identifier);
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
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (filter.test(yamlConfiguration)) {
            try {
                T asset = readFunction.apply(yamlConfiguration, locale, fileName);
                if (asset == null)
                    return;
                addOrCreateLocale(asset, fileName);
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
                T asset = readFunction.apply(section, locale, reference);
                addOrCreateLocale(asset, reference);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (filter.test(yamlConfiguration)) {
            try {
                T asset = readFunction.apply(yamlConfiguration, locale, fileName);
                if (asset == null)
                    return;
                addOrCreateLocale(asset, fileName);
                assets.get(plugin.getName()).add(fileName);
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
                T asset = readFunction.apply(section, locale, reference);
                addOrCreateLocale(asset, reference);
                assets.get(plugin.getName()).add(reference);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + file.getPath());
                throwable.printStackTrace();
            }
        });
    }

    private boolean addOrCreateLocale(T asset, String reference) {
        String locale = asset.locale();
        Map<String, T> localeMap = locales.computeIfAbsent(locale, k -> new HashMap<>());
        if (localeMap.containsKey(reference)) {
            addDuplicate(reference);
            return false;
        }
        localeMap.put(reference, asset);
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
        Map<String, T> map = locales.get("en_us");
        if (map == null)
            return null;
        return map.get(identifier);
    }

    public List<T> getAssets(@NotNull String locale) {
        Objects.requireNonNull(locale);
        @Nullable Map<String, T> english = locales.get("en_us");
        Map<String, T> copy = new HashMap<>();
        if (english != null)
            copy.putAll(english);
        Map<String, T> map = locales.get(locale);
        if (map != null)
            copy.putAll(map);
        return copy.values().stream().toList();
    }

    public Map<String, T> getDefault() {
        @Nullable Map<String, T> english = locales.get("en_us");
        Map<String, T> copy = new HashMap<>();
        if (english != null)
            copy.putAll(english);
        return copy;
    }

    public List<T> getAssets() {
        return getAssets("en_us");
    }

    @Nullable
    public T getAsset(@NotNull String identifier,
                      @NotNull String locale) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(locale);
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        Map<String, T> localeMap = locales.get(locale);
        if (localeMap == null || !localeMap.containsKey(identifier))
            localeMap = locales.get("en_us");
        if (localeMap == null)
            return null;
        return localeMap.get(identifier);
    }

    @NotNull
    public File getAssetDirectory() {
        return assetDirectory;
    }
}
