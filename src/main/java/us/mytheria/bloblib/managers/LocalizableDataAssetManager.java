package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.DataAsset;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.entities.Localizable;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class LocalizableDataAssetManager<T extends DataAsset & Localizable> {
    private final File assetDirectory;
    private final TriFunction<ConfigurationSection, String, String, T> readFunction;
    private final DataAssetType type;
    private final Predicate<ConfigurationSection> filter;

    private final BlobLib main;
    private HashMap<String, Set<String>> assets;
    private HashMap<String, Integer> duplicates;
    private HashMap<String, Map<String, T>> locales;

    /**
     * Creates a new instance of the LocalizableDataAssetManager
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset.
     *                       Think of it as checks that once met, the ConfigurationSection
     *                       is considered from an asset.
     * @param <T>            The type of the asset
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static <T extends DataAsset & Localizable> LocalizableDataAssetManager<T> of(@NotNull File assetDirectory,
                                                                                        @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                                                                        @NotNull DataAssetType type,
                                                                                        @NotNull Predicate<ConfigurationSection> filter) {
        Objects.requireNonNull(assetDirectory, "Asset directory cannot be null");
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        Objects.requireNonNull(type, "Data asset type cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        if (!assetDirectory.isDirectory())
            throw new IllegalArgumentException("File '" + assetDirectory.getPath() + "' is not a directory");
        return new LocalizableDataAssetManager<>(assetDirectory,
                readFunction, type, filter);
    }

    private LocalizableDataAssetManager(@NotNull File assetDirectory,
                                        @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                        @NotNull DataAssetType type,
                                        Predicate<ConfigurationSection> filter) {
        this.main = BlobLib.getInstance();
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
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + key + "' (found " + value + " instances)"));
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
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate " + type.name() + ": '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        this.assets.remove(pluginName);
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
        String locale = asset.getLocale();
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
        Map<String, T> map = locales.get("en_us");
        if (map == null)
            return null;
        return map.get(key);
    }

    public List<T> getAssets(@NotNull String locale) {
        Objects.requireNonNull(locale);
        Map<String, T> map = locales.get(locale);
        if (map == null)
            return new ArrayList<>();
        return new ArrayList<>(map.values());
    }

    public List<T> getAssets() {
        return getAssets("en_us");
    }

    @Nullable
    public T getAsset(@NotNull String key,
                      @NotNull String locale) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(locale);
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        Map<String, T> localeMap = locales.get(locale);
        if (localeMap == null || !localeMap.containsKey(key))
            localeMap = locales.get("en_us");
        if (localeMap == null)
            return null;
        return localeMap.get(key);
    }
}
