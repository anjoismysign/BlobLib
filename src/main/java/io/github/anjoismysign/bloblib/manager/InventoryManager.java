package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.inventory.BlobInventory;
import io.github.anjoismysign.bloblib.inventory.InventoryBuilderCarrier;
import io.github.anjoismysign.bloblib.inventory.InventoryButton;
import io.github.anjoismysign.bloblib.inventory.InventoryDataRegistry;
import io.github.anjoismysign.bloblib.inventory.MetaBlobInventory;
import io.github.anjoismysign.bloblib.inventory.MetaInventoryButton;
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
                        (section, locale, reference, filePath) -> InventoryBuilderCarrier
                                .BLOB_FROM_CONFIGURATION_SECTION(section, reference, filePath)
                                .setLocale(locale),
                        DataAssetType.BLOB_INVENTORY,
                        section -> section.isInt("Size"));
        this.metaInventoryManager = LocalizableDataAssetManager
                .of(BlobLib.getInstance().getFileManager().getDirectory(DataAssetType.META_BLOB_INVENTORY),
                        (section, locale, reference, filePath) -> {
                            InventoryBuilderCarrier<MetaInventoryButton> carrier = InventoryBuilderCarrier
                                    .META_FROM_CONFIGURATION_SECTION(section, reference, filePath)
                                    .setLocale(locale);
                            shards.computeIfAbsent(carrier.type(), _ -> new MetaInventoryShard())
                                    .addInventory(carrier, reference);
                            return carrier;
                        },
                        DataAssetType.META_BLOB_INVENTORY,
                        section -> section.isInt("Size"));
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
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        blobInventoryManager.reload(plugin, director);
        metaInventoryManager.reload(plugin, director);
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
        return metaInventoryManager.getAsset(key, locale);
    }

    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return metaInventoryManager.getAsset(key);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key, String locale) {
        @Nullable InventoryBuilderCarrier<MetaInventoryButton> carrier = metaInventoryManager.getAsset(key, locale);
        if (carrier == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory getMetaInventory(String key) {
        @Nullable InventoryBuilderCarrier<MetaInventoryButton> carrier = metaInventoryManager.getAsset(key);
        if (carrier == null)
            return null;
        return MetaBlobInventory.fromInventoryBuilderCarrier(carrier);
    }

    @Nullable
    public MetaBlobInventory cloneMetaInventory(String key, String locale) {
        MetaBlobInventory inventory = getMetaInventory(key, locale);
        if (inventory == null)
            return null;
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
        return shards.get(type);
    }
}
