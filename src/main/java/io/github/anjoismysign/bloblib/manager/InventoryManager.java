package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.content.LocaleOverlay;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.inventory.BlobInventory;
import io.github.anjoismysign.bloblib.inventory.InventoryBuilderCarrier;
import io.github.anjoismysign.bloblib.inventory.InventoryButton;
import io.github.anjoismysign.bloblib.inventory.InventoryDataRegistry;
import io.github.anjoismysign.bloblib.inventory.InventoryOverlay;
import io.github.anjoismysign.bloblib.inventory.MetaBlobInventory;
import io.github.anjoismysign.bloblib.inventory.MetaInventoryButton;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds both inventory asset managers and the {@link InventoryDataRegistry} of each key,
 * which is where click/close events of an inventory are registered.
 */
public class InventoryManager {
    private final LocalizableDataAssetManager<InventoryBuilderCarrier<InventoryButton>> blobInventoryManager;
    private final LocalizableDataAssetManager<InventoryBuilderCarrier<MetaInventoryButton>> metaInventoryManager;
    private final Map<String, MetaInventoryShard> shards;
    private final Map<String, InventoryDataRegistry<InventoryButton>> blobRegistries;
    private final Map<String, InventoryDataRegistry<MetaInventoryButton>> metaRegistries;

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.load(plugin, director);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        InventoryManager manager = BlobLib.getInstance().getInventoryManager();
        manager.unload(plugin);
    }

    public static void continueLoadingBlobInventories(BlobPlugin plugin, File... files) {
        BlobLib.getInstance().getInventoryManager().blobInventoryManager
                .continueLoadingAssets(plugin, true, files);
    }

    public static void continueLoadingMetaInventories(BlobPlugin plugin, File... files) {
        BlobLib.getInstance().getInventoryManager().metaInventoryManager
                .continueLoadingAssets(plugin, true, files);
    }

    public InventoryManager() {
        this.shards = new HashMap<>();
        this.blobRegistries = new HashMap<>();
        this.metaRegistries = new HashMap<>();
        this.blobInventoryManager = LocalizableDataAssetManager
                .of(BlobLib.getInstance().getFileManager().getDirectory(DataAssetType.BLOB_INVENTORY),
                        (LocalizableDataAssetManager.AssetReader<InventoryBuilderCarrier<InventoryButton>>) this::read,
                        DataAssetType.BLOB_INVENTORY,
                        InventoryManager::isInventory)
                .overlaying(overlayHandler(DataAssetType.BLOB_INVENTORY, this::read));
        this.metaInventoryManager = LocalizableDataAssetManager
                .of(BlobLib.getInstance().getFileManager().getDirectory(DataAssetType.META_BLOB_INVENTORY),
                        (LocalizableDataAssetManager.AssetReader<InventoryBuilderCarrier<MetaInventoryButton>>) (section, locale, reference, filePath) -> {
                            InventoryBuilderCarrier<MetaInventoryButton> carrier = readMeta(section, locale, reference, filePath);
                            shards.computeIfAbsent(carrier.type(), _ -> new MetaInventoryShard())
                                    .addInventory(carrier, reference);
                            return carrier;
                        },
                        DataAssetType.META_BLOB_INVENTORY,
                        InventoryManager::isInventory)
                .overlaying(overlayHandler(DataAssetType.META_BLOB_INVENTORY, this::readMeta));
    }

    /**
     * An inventory of the default locale is recognized by its 'Size', which an overlay
     * does not carry: a locale overlay is recognized by the text it translates.
     */
    private static boolean isInventory(@NotNull ConfigurationSection section,
                                       @NotNull String locale) {
        return LocaleOverlay.isDefault(locale)
                ? section.isInt("Size")
                : section.isString("Title") || section.isConfigurationSection("Buttons");
    }

    /**
     * Builds the handler that merges the locale overlays of an inventory onto the section
     * its en_us counterpart was read from, and reads the result with the very same factory.
     * <p>
     * An overlay is never added to a {@link MetaInventoryShard}, since a shard is keyed by
     * reference alone and would have its en_us carrier overwritten. Only the default locale
     * reader above touches the shards, so this comes for free.
     */
    @NotNull
    private <T extends InventoryButton> LocalizableDataAssetManager.OverlayHandler<InventoryBuilderCarrier<T>> overlayHandler(
            @NotNull DataAssetType type,
            @NotNull LocalizableDataAssetManager.AssetReader<InventoryBuilderCarrier<T>> reader) {
        return new LocalizableDataAssetManager.OverlayHandler<>() {
            @Override
            public void validate(@NotNull ConfigurationSection section,
                                 @NotNull String locale,
                                 @NotNull String reference,
                                 @NotNull String filePath) {
                InventoryOverlay.read(type, section, locale, reference, filePath);
            }

            @Override
            public InventoryBuilderCarrier<T> merge(@Nullable InventoryBuilderCarrier<T> baseAsset,
                                                    @NotNull ConfigurationSection baseSection,
                                                    @NotNull ConfigurationSection section,
                                                    @NotNull String locale,
                                                    @NotNull String reference,
                                                    @NotNull String filePath) {
                InventoryOverlay.Pending overlay = new InventoryOverlay.Pending(type, reference, locale, filePath, section);
                return reader.read(InventoryOverlay.merge(baseSection, overlay), locale, reference, filePath);
            }
        };
    }

    @NotNull
    private InventoryBuilderCarrier<InventoryButton> read(@NotNull ConfigurationSection section,
                                                          @NotNull String locale,
                                                          @NotNull String reference,
                                                          @NotNull String filePath) {
        return InventoryBuilderCarrier
                .BLOB_FROM_CONFIGURATION_SECTION(section, reference, filePath)
                .setLocale(locale);
    }

    @NotNull
    private InventoryBuilderCarrier<MetaInventoryButton> readMeta(@NotNull ConfigurationSection section,
                                                                  @NotNull String locale,
                                                                  @NotNull String reference,
                                                                  @NotNull String filePath) {
        return InventoryBuilderCarrier
                .META_FROM_CONFIGURATION_SECTION(section, reference, filePath)
                .setLocale(locale);
    }

    /**
     * Builds every inventory locale overlay that has been read but not merged yet.
     */
    public void materializeOverlays() {
        blobInventoryManager.materializeOverlays();
        metaInventoryManager.materializeOverlays();
    }

    /**
     * @return The manager of all BlobInventories
     */
    @NotNull
    public LocalizableDataAssetManager<InventoryBuilderCarrier<InventoryButton>> getBlobInventoryManager() {
        return blobInventoryManager;
    }

    /**
     * @return The manager of all MetaBlobInventories
     */
    @NotNull
    public LocalizableDataAssetManager<InventoryBuilderCarrier<MetaInventoryButton>> getMetaInventoryManager() {
        return metaInventoryManager;
    }

    public void reload() {
        shards.clear();
        blobRegistries.clear();
        metaRegistries.clear();
        blobInventoryManager.reload();
        metaInventoryManager.reload();
        materializeOverlays();
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        blobInventoryManager.reload(plugin, director);
        metaInventoryManager.reload(plugin, director);
        materializeOverlays();
    }

    public void unload(BlobPlugin plugin) {
        blobInventoryManager.unload(plugin);
        metaInventoryManager.unload(plugin);
    }

    /**
     * @return An unmodifiable map of all blob inventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<InventoryButton>> getBlobInventories() {
        blobInventoryManager.getIdentifiers().forEach(this::getInventoryDataRegistry);
        return Collections.unmodifiableMap(blobRegistries);
    }

    /**
     * @return An unmodifiable map of all meta inventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<MetaInventoryButton>> getMetaInventories() {
        metaInventoryManager.getIdentifiers().forEach(this::getMetaInventoryDataRegistry);
        return Collections.unmodifiableMap(metaRegistries);
    }

    @Nullable
    public InventoryDataRegistry<InventoryButton> getInventoryDataRegistry(String key) {
        if (blobInventoryManager.getAsset(key) == null)
            return null;
        return blobRegistries.computeIfAbsent(key, k -> InventoryDataRegistry
                .of("en_us", k, (registryKey, locale) -> blobInventoryManager.getAsset(registryKey, locale)));
    }

    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key, String locale) {
        materializeOverlays();
        return blobInventoryManager.getAsset(key, locale);
    }

    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key) {
        return blobInventoryManager.getAsset(key);
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
        if (metaInventoryManager.getAsset(key) == null)
            return null;
        return metaRegistries.computeIfAbsent(key, k -> InventoryDataRegistry
                .of("en_us", k, (registryKey, locale) -> metaInventoryManager.getAsset(registryKey, locale)));
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key, String locale) {
        materializeOverlays();
        return metaInventoryManager.getAsset(key, locale);
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return metaInventoryManager.getAsset(key);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key, String locale) {
        materializeOverlays();
        @Nullable InventoryBuilderCarrier<MetaInventoryButton> carrier = metaInventoryManager.getAsset(key, locale);
        if (carrier == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key) {
        @Nullable InventoryBuilderCarrier<MetaInventoryButton> carrier = metaInventoryManager.getAsset(key);
        if (carrier == null) {
            return null;
        }
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory cloneMetaInventory(String key, String locale) {
        MetaBlobInventory inventory = getMetaInventory(key, locale);
        if (inventory == null) {
            return null;
        }
        return inventory.copy();
    }

    @Nullable
    public MetaBlobInventory cloneMetaInventory(String key) {
        MetaBlobInventory inventory = getMetaInventory(key);
        if (inventory == null) {
            return null;
        }
        return inventory.copy();
    }

    @Nullable
    public MetaInventoryShard getMetaInventoryShard(String type) {
        return shards.get(type);
    }
}
