package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.anjo.entities.Uber;
import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.BlobEditor;
import io.github.anjoismysign.bloblib.entities.BlobSelector;
import io.github.anjoismysign.bloblib.entities.inventory.BlobInventory;
import io.github.anjoismysign.bloblib.entities.inventory.BlobInventoryTracker;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryBuilderCarrier;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryButton;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryDataRegistry;
import io.github.anjoismysign.bloblib.entities.inventory.InventoryTracker;
import io.github.anjoismysign.bloblib.entities.inventory.MetaBlobInventoryTracker;
import io.github.anjoismysign.bloblib.entities.inventory.MetaInventoryButton;
import io.github.anjoismysign.bloblib.managers.InventoryManager;
import io.github.anjoismysign.bloblib.managers.InventoryTrackerManager;
import io.github.anjoismysign.bloblib.managers.MetaInventoryShard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlobLibInventoryAPI {
    private static BlobLibInventoryAPI instance;
    private final BlobLib plugin;

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

    private BlobLibInventoryAPI(BlobLib plugin) {
        this.plugin = plugin;
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
                                              @Nullable String dataType,
                                              @NotNull Supplier<List<T>> selectorList,
                                              @NotNull Consumer<T> onSelect,
                                              @Nullable Function<T, ItemStack> display,
                                              @Nullable Consumer<Player> onReturn,
                                              @Nullable Consumer<Player> onClose,
                                              @Nullable String clickSound) {
        BlobInventory inventory = getInventoryManager().cloneInventory(blobInventoryKey, player.getLocale());
        BlobSelector<T> selector = BlobSelector.build(inventory, player.getUniqueId(),
                dataType, selectorList.get(), onReturn);
        selector.setButtonRangeKey(buttonRangeKey);
        selector.setItemsPerPage(selector.getSlots(buttonRangeKey)
                == null ? 1 : selector.getSlots(buttonRangeKey).size());
        if (display != null)
            selector.selectElement(player,
                    onSelect,
                    null,
                    display,
                    selectorList::get,
                    onClose,
                    clickSound);
        else
            selector.selectElement(player,
                    onSelect,
                    null);
        return selector;
    }

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
                display,
                null,
                null,
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
                                          @Nullable Consumer<Player> onClose,
                                          @Nullable String clickSound) {
        BlobInventory inventory = getInventoryManager().cloneInventory(blobInventoryKey, player.getLocale());
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
                                        onClose,
                                        clickSound);
                            });
                    playerSelector.setItemsPerPage(playerSelector.getSlots(buttonRangeKey)
                            == null ? 1 : playerSelector.getSlots(buttonRangeKey).size());
                    playerSelector.selectElement(player,
                            onAdd,
                            null,
                            addDisplay,
                            viewCollection,
                            onClose,
                            clickSound);
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
                null,
                null);
    }
}
