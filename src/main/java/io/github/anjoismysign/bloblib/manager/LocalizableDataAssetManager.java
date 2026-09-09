package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.content.LocaleOverlay;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.domain.Localizable;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocalizableDataAssetManager<T extends DataAsset & Localizable> implements BlobLibDataAssetManager<T> {
    /**
     * Reads an asset from a ConfigurationSection, being aware of the file it belongs to.
     *
     * @param <T> The type of the asset
     */
    @FunctionalInterface
    public interface AssetReader<T> {
        /**
         * @param section   The section the asset is read from
         * @param locale    The locale of the file the section belongs to
         * @param reference The identifier of the asset
         * @param filePath  The path of the file the section belongs to
         * @return The asset, or null if it should be skipped
         */
        @Nullable
        T read(@NotNull ConfigurationSection section,
               @NotNull String locale,
               @NotNull String reference,
               @NotNull String filePath);
    }

    /**
     * Builds the assets of a non default locale, which carry translatable text alone and
     * inherit every other field from the default locale (en_us) asset of the same reference.
     * <p>
     * Overlays cannot be built while their file is read, since the en_us file they inherit
     * from is not guaranteed to have loaded yet. They are validated at read time and merged
     * by {@link #materializeOverlays()}.
     *
     * @param <T> The type of the asset
     */
    public interface OverlayHandler<T> {
        /**
         * Inspects an overlay as its file is read, rejecting it if it would translate
         * nothing and registering a warning for every field it declares that has no effect.
         *
         * @param section   The section the overlay is read from
         * @param locale    The locale of the overlay file
         * @param reference The identifier of the asset
         * @param filePath  The path of the overlay file
         */
        void validate(@NotNull ConfigurationSection section,
                      @NotNull String locale,
                      @NotNull String reference,
                      @NotNull String filePath);

        /**
         * Builds the asset of a non default locale from the default locale one.
         *
         * @param baseAsset   The asset of the default locale, null if it failed to load
         * @param baseSection The section the default locale asset was read from
         * @param section     The section the overlay is read from
         * @param locale      The locale of the overlay file
         * @param reference   The identifier of the asset
         * @param filePath    The path of the overlay file
         * @return The asset of that locale, or null if it should be skipped
         */
        @Nullable
        T merge(@Nullable T baseAsset,
                @NotNull ConfigurationSection baseSection,
                @NotNull ConfigurationSection section,
                @NotNull String locale,
                @NotNull String reference,
                @NotNull String filePath);
    }

    private record PendingOverlay(@NotNull String reference,
                                  @NotNull String locale,
                                  @NotNull String filePath,
                                  @NotNull ConfigurationSection section) {
    }

    private final File assetDirectory;
    private final AssetReader<T> readFunction;
    private final DataAssetType type;
    private final BiPredicate<ConfigurationSection, String> filter;
    @Nullable
    private OverlayHandler<T> overlayHandler;
    private final Map<String, ConfigurationSection> defaultSections = new HashMap<>();
    private final List<PendingOverlay> pendingOverlays = new ArrayList<>();

    private final BlobLib plugin;
    private final Logger logger;
    private Map<String, Set<String>> assets;
    private Map<String, List<String>> duplicates;
    private Map<String, String> keyFirstFile;
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
     * @param <T>            The type of the asset
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static <T extends DataAsset & Localizable> LocalizableDataAssetManager<T> of(@NotNull File assetDirectory,
                                                                                        @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                                                                        @NotNull DataAssetType type,
                                                                                        @NotNull Predicate<ConfigurationSection> filter) {
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        return of(assetDirectory,
                (section, locale, reference, filePath) -> readFunction.apply(section, locale, reference),
                type, filter);
    }

    /**
     * Creates a new instance of the LocalizableDataAssetManager whose read function
     * is aware of the path of the file the asset is being read from.
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset.
     * @param <T>            The type of the asset
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static <T extends DataAsset & Localizable> LocalizableDataAssetManager<T> of(@NotNull File assetDirectory,
                                                                                        @NotNull AssetReader<T> readFunction,
                                                                                        @NotNull DataAssetType type,
                                                                                        @NotNull Predicate<ConfigurationSection> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        return of(assetDirectory, readFunction, type, (section, locale) -> filter.test(section));
    }

    /**
     * Creates a new instance of the LocalizableDataAssetManager whose filter is aware
     * of the locale of the file the ConfigurationSection belongs to.
     * <p>
     * This allows a data asset type to accept 'locale overlay' files: files of a
     * non-default locale that carry only the translatable fields, while the
     * non-translatable data lives solely in the default locale ({@code en_us}) file.
     *
     * @param assetDirectory The directory where the assets are located
     * @param readFunction   The function that will read the assets
     * @param type           The type of the asset
     * @param filter         The filter that if true will load the asset, receiving
     *                       the ConfigurationSection and the locale of its file.
     * @param <T>            The type of the asset
     * @return The new instance of the LocalizableDataAssetManager
     */
    public static <T extends DataAsset & Localizable> LocalizableDataAssetManager<T> of(@NotNull File assetDirectory,
                                                                                        @NotNull AssetReader<T> readFunction,
                                                                                        @NotNull DataAssetType type,
                                                                                        @NotNull BiPredicate<ConfigurationSection, String> filter) {
        Objects.requireNonNull(assetDirectory, "Asset directory cannot be null");
        Objects.requireNonNull(readFunction, "Read function cannot be null");
        Objects.requireNonNull(type, "Data asset type cannot be null");
        Objects.requireNonNull(filter, "Filter cannot be null");
        if (!assetDirectory.isDirectory()) {
            assetDirectory.mkdirs();
        }
        return new LocalizableDataAssetManager<>(assetDirectory,
                readFunction, type, filter);
    }

    LocalizableDataAssetManager(@NotNull File assetDirectory,
                                @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                @NotNull DataAssetType type,
                                @NotNull Predicate<ConfigurationSection> filter) {
        this(assetDirectory,
                (section, locale, reference, filePath) -> readFunction.apply(section, locale, reference),
                type, (section, locale) -> filter.test(section));
    }

    LocalizableDataAssetManager(@NotNull File assetDirectory,
                                @NotNull TriFunction<ConfigurationSection, String, String, T> readFunction,
                                @NotNull DataAssetType type,
                                @NotNull BiPredicate<ConfigurationSection, String> filter) {
        this(assetDirectory,
                (section, locale, reference, filePath) -> readFunction.apply(section, locale, reference),
                type, filter);
    }

    LocalizableDataAssetManager(@NotNull File assetDirectory,
                                @NotNull AssetReader<T> readFunction,
                                @NotNull DataAssetType type,
                                @NotNull Predicate<ConfigurationSection> filter) {
        this(assetDirectory, readFunction, type, (section, locale) -> filter.test(section));
    }

    LocalizableDataAssetManager(@NotNull File assetDirectory,
                                @NotNull AssetReader<T> readFunction,
                                @NotNull DataAssetType type,
                                @NotNull BiPredicate<ConfigurationSection, String> filter) {
        this.plugin = BlobLib.getInstance();
        this.logger = plugin.getLogger();
        this.assetDirectory = assetDirectory;
        this.readFunction = readFunction;
        this.type = type;
        this.filter = filter;
    }

    public void reload() {
        defaultSections.clear();
        pendingOverlays.clear();
        locales = new HashMap<>();
        assets = new HashMap<>();
        duplicates = new HashMap<>();
        keyFirstFile = new HashMap<>();
        loadFiles(assetDirectory);
        duplicates.forEach((identifier, paths) -> logger
                .warning("Duplicate " + type.name() + ": '" + identifier + "' (found " + paths.size() + " instances)\n" +
                        paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
    }

    public void reload(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (assets.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        assets.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().getDirectory(type);
        loadFiles(directory, plugin);
        duplicates.forEach((identifier, paths) -> plugin.getLogger()
                .warning("Duplicate " + type.name() + ": '" + identifier + "' (found " + paths.size() + " instances)\n" +
                        paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        @Nullable Set<String> references = this.assets.remove(pluginName);
        if (references == null)
            return;
        for (String reference : references) {
            locales.values().forEach(localeMap -> localeMap.remove(reference));
            keyFirstFile.remove(reference);
        }
    }

    private void loadFiles(File directory) {
        loadFiles(directory, null);
    }

    private void loadFiles(File directory, @Nullable BlobPlugin plugin) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                try {
                    if (plugin == null)
                        loadYamlConfiguration(file);
                    else
                        loadYamlConfiguration(file, plugin);
                } catch (ConfigurationFieldException exception) {
                    this.plugin.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadFiles(file, plugin);
        }
    }

    private void loadYamlConfiguration(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String filePath = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (filter.test(yamlConfiguration, locale)) {
            try {
                T asset = readAsset(yamlConfiguration, locale, fileName, filePath);
                if (asset == null) {
                    return;
                }
                addOrCreateLocale(asset, fileName, filePath);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + filePath);
                throwable.printStackTrace();
            }
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!filter.test(section, locale))
                return;
            try {
                T asset = readAsset(section, locale, reference, filePath);
                if (asset == null)
                    return;
                addOrCreateLocale(asset, reference, filePath);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + filePath);
                throwable.printStackTrace();
            }
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        String filePath = file.getPath();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (filter.test(yamlConfiguration, locale)) {
            try {
                T asset = readAsset(yamlConfiguration, locale, fileName, filePath);
                if (asset == null)
                    return;
                addOrCreateLocale(asset, fileName, filePath);
                assets.computeIfAbsent(plugin.getName(), k -> new HashSet<>()).add(fileName);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + filePath);
                throwable.printStackTrace();
            }
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!filter.test(section, locale))
                return;
            try {
                T asset = readAsset(section, locale, reference, filePath);
                if (asset == null)
                    return;
                addOrCreateLocale(asset, reference, filePath);
                assets.computeIfAbsent(plugin.getName(), k -> new HashSet<>()).add(reference);
            } catch (Throwable throwable) {
                BlobLib.getInstance().getLogger().severe("At: " + filePath);
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Turns this manager into one whose non default locale files are locale overlays.
     *
     * @param overlayHandler The handler that validates and merges them
     * @return This manager
     */
    @NotNull
    public LocalizableDataAssetManager<T> overlaying(@NotNull OverlayHandler<T> overlayHandler) {
        this.overlayHandler = Objects.requireNonNull(overlayHandler, "'overlayHandler' cannot be null");
        return this;
    }

    /**
     * Builds every locale overlay that has been read but not merged yet, now that the
     * en_us file it inherits from is expected to have loaded.
     * <p>
     * Draining is idempotent, so this may be called as often as is convenient.
     */
    public void materializeOverlays() {
        if (overlayHandler == null || pendingOverlays.isEmpty()) {
            return;
        }
        List<PendingOverlay> pending = new ArrayList<>(pendingOverlays);
        pendingOverlays.clear();
        @Nullable Map<String, T> english = locales.get(LocaleOverlay.DEFAULT_LOCALE);
        for (PendingOverlay overlay : pending) {
            @Nullable ConfigurationSection baseSection = defaultSections.get(overlay.reference());
            if (baseSection == null) {
                plugin.getLogger().severe("No default locale (" + LocaleOverlay.DEFAULT_LOCALE +
                        ") provided for '" + overlay.reference() + "' " + type.name() +
                        "\nAt: " + overlay.filePath());
                continue;
            }
            try {
                @Nullable T asset = overlayHandler.merge(english == null ? null : english.get(overlay.reference()),
                        baseSection, overlay.section(), overlay.locale(), overlay.reference(), overlay.filePath());
                if (asset == null) {
                    continue;
                }
                addOrCreateLocale(asset, overlay.reference(), overlay.filePath());
            } catch (ConfigurationFieldException exception) {
                plugin.getLogger().severe(exception.getMessage() + "\nAt: " + overlay.filePath());
            } catch (Throwable throwable) {
                plugin.getLogger().severe("At: " + overlay.filePath());
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Reads an asset, holding back the overlays of a manager that has an
     * {@link OverlayHandler} until every file has been read.
     */
    @Nullable
    private T readAsset(@NotNull ConfigurationSection section,
                        @NotNull String locale,
                        @NotNull String reference,
                        @NotNull String filePath) {
        if (overlayHandler == null)
            return readFunction.read(section, locale, reference, filePath);
        if (!LocaleOverlay.isDefault(locale)) {
            overlayHandler.validate(section, locale, reference, filePath);
            pendingOverlays.add(new PendingOverlay(reference, locale, filePath, section));
            return null;
        }
        defaultSections.put(reference, section);
        return readFunction.read(section, locale, reference, filePath);
    }

    /**
     * Adds an asset that was not built while its file was being read, such as a
     * locale overlay that had to wait for its default locale counterpart to load.
     *
     * @param asset     The asset to add
     * @param reference The identifier of the asset
     * @param filePath  The path of the file the asset was read from
     * @return true if it was added, false if the reference was already held for its locale
     */
    public boolean addAsset(@NotNull T asset,
                            @NotNull String reference,
                            @NotNull String filePath) {
        Objects.requireNonNull(asset, "'asset' cannot be null");
        Objects.requireNonNull(reference, "'reference' cannot be null");
        Objects.requireNonNull(filePath, "'filePath' cannot be null");
        return addOrCreateLocale(asset, reference, filePath);
    }

    private boolean addOrCreateLocale(T asset, String reference, String filePath) {
        String locale = asset.locale();
        Map<String, T> localeMap = locales.computeIfAbsent(locale, k -> new HashMap<>());
        if (localeMap.containsKey(reference)) {
            addDuplicate(reference, filePath);
            return false;
        }
        localeMap.put(reference, asset);
        keyFirstFile.put(reference, filePath);
        return true;
    }

    public void continueLoadingAssets(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        duplicates.clear();
        for (File file : files)
            loadYamlConfiguration(file, plugin);
        if (warnDuplicates)
            duplicates.forEach((identifier, paths) -> plugin.getLogger()
                    .warning("Duplicate " + type.name() + ": '" + identifier + "' (found " + paths.size() + " instances)\n" +
                            paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
    }

    private void addDuplicate(String identifier, String filePath) {
        duplicates.computeIfAbsent(identifier, k -> {
            List<String> list = new ArrayList<>();
            list.add(keyFirstFile.getOrDefault(k, "unknown"));
            return list;
        }).add(filePath);
    }

    @Nullable
    public T getAsset(@NotNull String identifier) {
        Objects.requireNonNull(identifier);
        return getAsset(identifier, "en_us");
    }

    public List<T> getAssets(@NotNull String locale) {
        materializeOverlays();
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
        materializeOverlays();
        @Nullable Map<String, T> english = locales.get("en_us");
        Map<String, T> copy = new HashMap<>();
        if (english != null)
            copy.putAll(english);
        return copy;
    }

    @Nullable
    public T getAsset(@NotNull String identifier,
                      @NotNull String locale) {
        materializeOverlays();
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

    /**
     * @return All identifiers held by this manager, across every locale
     */
    @NotNull
    public Set<String> getIdentifiers() {
        materializeOverlays();
        Set<String> identifiers = new HashSet<>();
        locales.values().forEach(localeMap -> identifiers.addAll(localeMap.keySet()));
        return identifiers;
    }

    @NotNull
    public File getAssetDirectory() {
        return assetDirectory;
    }
}
