package us.mytheria.bloblib.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.*;
import us.mytheria.bloblib.managers.InventoryManager;
import us.mytheria.bloblib.managers.MetaInventoryShard;

import java.io.File;
import java.util.Optional;

public class BlobLibInventoryAPI {
    private static BlobLibInventoryAPI instance;
    private final BlobLib plugin;

    private BlobLibInventoryAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibInventoryAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibInventoryAPI.instance = new BlobLibInventoryAPI(plugin);
        }
        return instance;
    }

    public static BlobLibInventoryAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The inventory manager
     */
    public InventoryManager getInventoryManager() {
        return plugin.getInventoryManager();
    }

    /**
     * @param key Key that points to the carrier
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key) {
        return getInventoryManager().getInventoryBuilderCarrier(key);
    }

    /**
     * Will search for an InventoryBuilderCarrier with the given key.
     * If found, will attempt to build the inventory.
     *
     * @param key Key that points to the inventory
     * @return The inventory
     */
    @Nullable
    public BlobInventory getBlobInventory(String key) {
        return getInventoryManager().getInventory(key);
    }

    /**
     * @param key Key that points to the carrier
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key) {
        return getInventoryManager().getMetaInventoryBuilderCarrier(key);
    }

    /**
     * Will search for an InventoryBuilderCarrier with the given key.
     * If found, will attempt to build the inventory.
     *
     * @param key Key that points to the inventory
     * @return The inventory
     */
    @Nullable
    public MetaBlobInventory getMetaBlobInventory(String key) {
        return getInventoryManager().getMetaInventory(key);
    }

    /**
     * Attempts to get a MetaInventoryShard from the given type.
     * If not found, will return an empty optional.
     * MUST BE SURE Optional#isPresent == true BEFORE CALLING get() ON IT!
     *
     * @param type The type of the shard
     * @return The shard if found, otherwise an empty optional
     */
    @NotNull
    public Optional<MetaInventoryShard> hasMetaInventoryShard(String type) {
        return Optional.ofNullable(getInventoryManager().getMetaInventoryShard(type));
    }

    /**
     * Retrieves a file from the inventories' directory.
     *
     * @return The inventories file
     */
    @NotNull
    public File getInventoriesDirectory() {
        return plugin.getFileManager().inventoriesDirectory();
    }

    /**
     * @return The inventories file path
     */
    @NotNull
    public String getInventoriesFilePath() {
        return plugin.getFileManager().inventoriesDirectory().getPath();
    }

    /**
     * Attempts to build an inventory from the given file name.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param fileName The file name
     * @return The inventory
     */
    public BlobInventory buildInventory(String fileName) {
        BlobInventory inventory = getInventoryManager().cloneInventory(fileName);
        if (inventory == null) {
            throw new NullPointerException("Inventory '" + fileName + "' not found");
        }
        return inventory;
    }
}
