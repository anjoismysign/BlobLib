package us.mytheria.bloblib.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.inventory.*;
import us.mytheria.bloblib.managers.InventoryManager;
import us.mytheria.bloblib.managers.InventoryTrackerManager;
import us.mytheria.bloblib.managers.MetaInventoryShard;

import java.io.File;
import java.util.Objects;
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
     * @return The inventory tracker manager
     */
    public InventoryTrackerManager getInventoryTrackerManager() {
        return plugin.getInventoryTrackerManager();
    }

    /**
     * @param key    Key that points to the carrier
     * @param locale The locale
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key, String locale) {
        return getInventoryManager().getInventoryBuilderCarrier(key, locale);
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
     * @param key    Key that points to the inventory
     * @param locale The locale
     * @return The inventory
     */
    @Nullable
    public BlobInventory getBlobInventory(String key, String locale) {
        return getInventoryManager().getInventory(key, locale);
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
     * @param key    Key that points to the carrier
     * @param locale The locale
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key, String locale) {
        return getInventoryManager().getMetaInventoryBuilderCarrier(key, locale);
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
     * @param key    Key that points to the inventory
     * @param locale The locale
     * @return The inventory
     */
    @Nullable
    public MetaBlobInventory getMetaBlobInventory(String key, String locale) {
        return getInventoryManager().getMetaInventory(key, locale);
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
     * Attempts to build an inventory from the given key.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key    Key that points to the inventory
     * @param locale The locale
     * @return The inventory
     */
    @NotNull
    public BlobInventory buildInventory(String key, String locale) {
        return Objects.requireNonNull(getInventoryManager()
                .cloneInventory(key, locale), "'" + key + "' is not a valid BlobInventory key");
    }

    /**
     * Attempts to build an inventory from the given key.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key Key that points to the inventory
     * @return The inventory
     */
    @NotNull
    public BlobInventory buildInventory(String key) {
        return Objects.requireNonNull(getInventoryManager()
                .cloneInventory(key), "'" + key + "' is not a valid BlobInventory key");
    }

    /**
     * Attempts to build a meta blob inventory from the given key.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key    Key that points to the inventory
     * @param locale The locale
     * @return The inventory
     */
    @NotNull
    public MetaBlobInventory buildMetaInventory(String key, String locale) {
        return Objects.requireNonNull(getInventoryManager()
                .cloneMetaInventory(key, locale), "'" + key + "' is not a valid MetaBlobInventory key");
    }

    /**
     * Attempts to build a MetaBlobInventory from the given key.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key Key that points to the inventory
     * @return The inventory
     */
    @NotNull
    public MetaBlobInventory buildMetaInventory(String key) {
        return Objects.requireNonNull(getInventoryManager()
                .cloneMetaInventory(key), "'" + key + "' is not a valid MetaBlobInventory key");
    }

    @Nullable
    public InventoryDataRegistry<InventoryButton> getInventoryDataRegistry(String key) {
        return getInventoryManager().getInventoryDataRegistry(key);
    }

    @Nullable
    public InventoryDataRegistry<MetaInventoryButton> getMetaInventoryDataRegistry(String key) {
        return getInventoryManager().getMetaInventoryDataRegistry(key);
    }

    /**
     * Will track the inventory of the specified player.
     * This tracker will dynamically update the inventory when the player's locale changes.
     * In order to use the live inventory (such as for opening),
     * you must call {@link InventoryTracker#getInventory()}
     * <p>
     * It's your responsibility holding the reference and freeing it when you don't need it anymore.
     *
     * @param player the player
     * @param key    the key of the inventory
     * @return the Tracker, null if the key is not found
     */
    @Nullable
    public BlobInventoryTracker trackInventory(@NotNull Player player, @NotNull String key) {
        return getInventoryTrackerManager().trackInventory(player, key);
    }

    /**
     * Will track the inventory of the specified player.
     * This tracker will dynamically update the inventory when the player's locale changes.
     * In order to use the live inventory (such as for opening),
     * you must call {@link InventoryTracker#getInventory()}
     * <p>
     * It's your responsibility holding the reference and freeing it when you don't need it anymore.
     *
     * @param player the player
     * @param key    the key of the inventory
     * @return the Tracker, null if the key is not found
     */
    @Nullable
    public MetaBlobInventoryTracker trackMetaInventory(@NotNull Player player, @NotNull String key) {
        return getInventoryTrackerManager().trackMetaInventory(player, key);
    }
}
