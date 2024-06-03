package us.mytheria.bloblib.api;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.BlobSelector;
import us.mytheria.bloblib.entities.inventory.*;
import us.mytheria.bloblib.managers.InventoryManager;
import us.mytheria.bloblib.managers.InventoryTrackerManager;
import us.mytheria.bloblib.managers.MetaInventoryShard;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * @return An unmodifiable map of all BlobInventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<InventoryButton>> getBlobInventories() {
        return getInventoryManager().getBlobInventories();
    }

    /**
     * @return An unmodifiable map of all MetaBlobInventories.
     */
    @NotNull
    public Map<String, InventoryDataRegistry<MetaInventoryButton>> getMetaBlobInventories() {
        return getInventoryManager().getMetaInventories();
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
     * Uses player to get locale
     *
     * @param key    Key that points to the carrier
     * @param player The player
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<InventoryButton> getInventoryBuilderCarrier(String key, Player player) {
        return getInventoryManager().getInventoryBuilderCarrier(key, player.getLocale());
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
     * Uses player to get locale
     *
     * @param key    Key that points to the carrier
     * @param player The player
     * @return The carrier if found. null otherwise
     */
    @Nullable
    public InventoryBuilderCarrier<MetaInventoryButton> getMetaInventoryBuilderCarrier(String key, Player player) {
        return getInventoryManager().getMetaInventoryBuilderCarrier(key, player.getLocale());
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

    /**
     * Will get a BlobInventory from the given key and Player locale
     *
     * @param key    the key of the inventory
     * @param player the player
     * @return the BlobInventory, null if the key is not found
     */
    @Nullable
    public BlobInventory getBlobInventory(@NotNull String key,
                                          @NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        return getBlobInventory(key, player.getLocale());
    }

    /**
     * Will get a MetaBlobInventory from the given key and Player locale
     *
     * @param key    the key of the inventory
     * @param player the player
     * @return the MetaBlobInventory, null if the key is not found
     */
    @Nullable
    public MetaBlobInventory getMetaBlobInventory(@NotNull String key,
                                                  @NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        return getMetaBlobInventory(key, player.getLocale());
    }

    /**
     * Attempts to build an inventory from the given key.
     * Will use the player's locale.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key    Key that points to the inventory
     * @param player the player
     * @return The inventory
     */
    @NotNull
    public BlobInventory buildInventory(@NotNull String key,
                                        @NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        return buildInventory(key, player.getLocale());
    }

    /**
     * Attempts to build a MetaBlobInventory from the given key.
     * Will use the player's locale.
     * If the inventory is not found, a NullPointerException is thrown.
     *
     * @param key    Key that points to the inventory
     * @param player the player
     * @return The inventory
     */
    @NotNull
    public MetaBlobInventory buildMetaInventory(@NotNull String key,
                                                @NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        return buildMetaInventory(key, player.getLocale());
    }

    /**
     * @deprecated Use {@link #customSelector(String, Player, String, String, Supplier, Consumer, Function, Consumer, Consumer)} instead.
     */
    @Deprecated
    @Nullable
    public <T> BlobSelector<T> customSelector(@NotNull String blobInventoryKey,
                                              @NotNull Player player,
                                              @NotNull String buttonRangeKey,
                                              @NotNull String dataType,
                                              @NotNull Supplier<List<T>> selectorList,
                                              @NotNull Consumer<T> onSelect,
                                              @Nullable Function<T, ItemStack> display) {
        return customSelector(blobInventoryKey,
                player,
                buttonRangeKey,
                dataType,
                selectorList,
                onSelect,
                display, null);
    }

    /**
     * @deprecated Use {@link #customSelector(String, Player, String, String, Supplier, Consumer, Function, Consumer, Consumer)} instead.
     */
    @Deprecated
    @Nullable
    public <T> BlobSelector<T> customSelector(@NotNull String blobInventoryKey,
                                              @NotNull Player player,
                                              @NotNull String buttonRangeKey,
                                              @NotNull String dataType,
                                              @NotNull Supplier<List<T>> selectorList,
                                              @NotNull Consumer<T> onSelect,
                                              @Nullable Function<T, ItemStack> display,
                                              @Nullable Consumer<Player> onReturn) {
        return customSelector(blobInventoryKey,
                player,
                buttonRangeKey,
                dataType,
                selectorList,
                onSelect,
                display,
                onReturn, null);
    }

    /**
     * Will make Player to select from a list of elements.
     * The selector will be placed in the inventory at the specified buttonRangeKey.
     * The inventory will open automatically.
     *
     * @param blobInventoryKey the key of the BlobInventory
     * @param player           the player
     * @param buttonRangeKey   the button from the BlobInventory which contains the slots (per page) into which the selector will place the elements
     * @param dataType         the data type of the selector
     * @param selectorList     the list of elements to select from
     * @param onSelect         what's consumed when an element is selected
     * @param display          the function to display an element, needs to return the ItemStack to display
     * @param onReturn         what's consumed when the player returns the selector
     * @param onClose          what's consumed when the player closes the selector
     * @param <T>              the type of the selector
     * @return the selector
     */
    @Nullable
    public <T> BlobSelector<T> customSelector(@NotNull String blobInventoryKey,
                                              @NotNull Player player,
                                              @NotNull String buttonRangeKey,
                                              @NotNull String dataType,
                                              @NotNull Supplier<List<T>> selectorList,
                                              @NotNull Consumer<T> onSelect,
                                              @Nullable Function<T, ItemStack> display,
                                              @Nullable Consumer<Player> onReturn,
                                              @Nullable Consumer<Player> onClose) {
        BlobInventory inventory = buildInventory(blobInventoryKey, player);
        BlobSelector<T> selector = BlobSelector.build(inventory, player.getUniqueId(),
                dataType, selectorList.get(), onReturn);
        selector.setItemsPerPage(selector.getSlots(buttonRangeKey)
                == null ? 1 : selector.getSlots(buttonRangeKey).size());
        selector.setWhiteBackgroundName(buttonRangeKey);
        if (display != null)
            selector.selectElement(player,
                    onSelect,
                    null,
                    display,
                    selectorList::get,
                    onClose);
        else
            selector.selectElement(player,
                    onSelect,
                    null);
        return selector;
    }

    /**
     * Will make Player to select from a list of elements.
     * The selector will be placed in the inventory at the specified buttonRangeKey.
     * The inventory will open automatically.
     * Will use default VariableSelector inventory.
     *
     * @param player       the player
     * @param dataType     the data type of the selector
     * @param selectorList the list of elements to select from
     * @param onSelect     what's consumed when an element is selected
     * @param display      the function to display an element, needs to return the ItemStack to display
     * @param <T>          the type of the selector
     * @return the selector
     */
    @Nullable
    public <T> BlobSelector<T> selector(@NotNull Player player,
                                        @NotNull String dataType,
                                        @NotNull Supplier<List<T>> selectorList,
                                        @NotNull Consumer<T> onSelect,
                                        @Nullable Function<T, ItemStack> display) {
        return customSelector("VariableSelector",
                player,
                "White-Background",
                dataType,
                selectorList,
                onSelect,
                display, null);
    }

    /**
     * @deprecated Use {@link #customEditor(String, Player, String, String, Supplier, Consumer, Function, Supplier, Function, Consumer, Consumer, Consumer)} instead.
     */
    @Deprecated
    @Nullable
    public <T> BlobEditor<T> customEditor(@NotNull String blobInventoryKey,
                                          @NotNull Player player,
                                          @NotNull String buttonRangeKey,
                                          @NotNull String dataType,
                                          @NotNull Supplier<Collection<T>> addCollection,
                                          @NotNull Consumer<T> onAdd,
                                          @Nullable Function<T, ItemStack> addDisplay,
                                          @NotNull Supplier<Collection<T>> viewCollection,
                                          @NotNull Function<T, ItemStack> removeDisplay,
                                          @NotNull Consumer<T> onRemove) {
        return customEditor(blobInventoryKey,
                player,
                buttonRangeKey,
                dataType,
                addCollection,
                onAdd,
                addDisplay,
                viewCollection,
                removeDisplay,
                onRemove,
                null,
                null);
    }

    /**
     * Will allow player to edit a collection of elements.
     * The editor will be placed in the inventory at the specified buttonRangeKey.
     * The inventory will open automatically.
     * Does nothing when the player closes the editor.
     *
     * @param blobInventoryKey the key of the BlobInventory
     * @param player           the player
     * @param buttonRangeKey   the button from the BlobInventory which contains the slots (per page) into which the editor will place the elements
     * @param dataType         the data type of the editor
     * @param addCollection    the collection of elements to add to
     * @param onAdd            what's consumed when an element is added
     * @param addDisplay       the function to display an element, needs to return the ItemStack to display
     * @param viewCollection   the collection of elements to view
     * @param removeDisplay    the function to display an element, needs to return the ItemStack to display
     * @param onRemove         what's consumed when an element is removed
     * @param onReturn         what's consumed when the player returns the editor
     * @param <T>              the type of the editor
     * @return the editor
     */
    @Nullable
    public <T> BlobEditor<T> customEditor(@NotNull String blobInventoryKey,
                                          @NotNull Player player,
                                          @NotNull String buttonRangeKey,
                                          @NotNull String dataType,
                                          @NotNull Supplier<Collection<T>> addCollection,
                                          @NotNull Consumer<T> onAdd,
                                          @Nullable Function<T, ItemStack> addDisplay,
                                          @NotNull Supplier<Collection<T>> viewCollection,
                                          @NotNull Function<T, ItemStack> removeDisplay,
                                          @NotNull Consumer<T> onRemove,
                                          @Nullable Consumer<Player> onReturn) {
        return customEditor(blobInventoryKey,
                player,
                buttonRangeKey,
                dataType,
                addCollection,
                onAdd,
                addDisplay,
                viewCollection,
                removeDisplay,
                onRemove,
                onReturn,
                null);
    }

    /**
     * Will allow player to edit a collection of elements.
     * The editor will be placed in the inventory at the specified buttonRangeKey.
     * The inventory will open automatically.
     *
     * @param blobInventoryKey the key of the BlobInventory
     * @param player           the player
     * @param buttonRangeKey   the button from the BlobInventory which contains the slots (per page) into which the editor will place the elements
     * @param dataType         the data type of the editor
     * @param addCollection    the collection of elements to add to
     * @param onAdd            what's consumed when an element is added
     * @param addDisplay       the function to display an element, needs to return the ItemStack to display
     * @param viewCollection   the collection of elements to view
     * @param removeDisplay    the function to display an element, needs to return the ItemStack to display
     * @param onRemove         what's consumed when an element is removed
     * @param onReturn         what's consumed when the player returns the editor
     * @param onClose          what's consumed when the player closes the editor
     * @param <T>              the type of the editor
     * @return the editor
     */
    @Nullable
    public <T> BlobEditor<T> customEditor(@NotNull String blobInventoryKey,
                                          @NotNull Player player,
                                          @NotNull String buttonRangeKey,
                                          @NotNull String dataType,
                                          @NotNull Supplier<Collection<T>> addCollection,
                                          @NotNull Consumer<T> onAdd,
                                          @Nullable Function<T, ItemStack> addDisplay,
                                          @NotNull Supplier<Collection<T>> viewCollection,
                                          @NotNull Function<T, ItemStack> removeDisplay,
                                          @NotNull Consumer<T> onRemove,
                                          @Nullable Consumer<Player> onReturn,
                                          @Nullable Consumer<Player> onClose) {
        BlobInventory inventory = buildInventory(blobInventoryKey, player);
        Uber<BlobEditor<T>> uber = Uber.fly();
        uber.talk(BlobEditor.build(inventory, player.getUniqueId(),
                dataType, owner -> {
                    BlobSelector<T> playerSelector = BlobSelector.COLLECTION_INJECTION(player.getUniqueId(),
                            dataType, addCollection.get(), player1 -> {
                                customEditor(blobInventoryKey,
                                        player,
                                        buttonRangeKey,
                                        dataType,
                                        addCollection,
                                        onAdd,
                                        addDisplay,
                                        viewCollection,
                                        removeDisplay,
                                        onRemove,
                                        onReturn,
                                        onClose);
                            });
                    playerSelector.setItemsPerPage(playerSelector.getSlots(buttonRangeKey)
                            == null ? 1 : playerSelector.getSlots(buttonRangeKey).size());
                    playerSelector.selectElement(player,
                            onAdd,
                            null,
                            addDisplay,
                            viewCollection,
                            onClose);
                }, viewCollection.get(), onReturn));
        BlobEditor<T> editor = uber.thanks();
        editor.setItemsPerPage(editor.getSlots(buttonRangeKey) == null
                ? 1 : editor.getSlots(buttonRangeKey).size());
        editor.manage(player,
                removeDisplay,
                onRemove);
        return editor;
    }

    /**
     * Will allow player to edit a collection of elements.
     * The editor will be placed in the inventory at the specified buttonRangeKey.
     * The inventory will open automatically.
     * Will use default BlobEditor inventory.
     *
     * @param player         the player
     * @param dataType       the data type of the editor
     * @param addCollection  the collection of elements to add to
     * @param onAdd          what's consumed when an element is added
     * @param addDisplay     the function to display an element, needs to return the ItemStack to display
     * @param viewCollection the collection of elements to view
     * @param removeDisplay  the function to display an element, needs to return the ItemStack to display
     * @param onRemove       what's consumed when an element is removed
     * @param <T>            the type of the editor
     * @return the editor
     */
    @Nullable
    public <T> BlobEditor<T> editor(@NotNull Player player,
                                    @NotNull String dataType,
                                    @NotNull Supplier<Collection<T>> addCollection,
                                    @NotNull Consumer<T> onAdd,
                                    @Nullable Function<T, ItemStack> addDisplay,
                                    @NotNull Supplier<Collection<T>> viewCollection,
                                    @NotNull Function<T, ItemStack> removeDisplay,
                                    @NotNull Consumer<T> onRemove) {
        return customEditor("BlobEditor",
                player,
                "White-Background",
                dataType,
                addCollection,
                onAdd,
                addDisplay,
                viewCollection,
                removeDisplay,
                onRemove,
                null,
                null);
    }
}
