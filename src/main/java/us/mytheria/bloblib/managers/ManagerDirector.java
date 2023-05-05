package us.mytheria.bloblib.managers;

import me.anjoismysign.anjo.logger.Logger;
import org.bukkit.event.Event;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.*;
import us.mytheria.bloblib.entities.currency.Currency;
import us.mytheria.bloblib.entities.currency.EconomyFactory;
import us.mytheria.bloblib.entities.currency.WalletOwner;
import us.mytheria.bloblib.entities.currency.WalletOwnerManager;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class ManagerDirector {
    private final BlobPlugin plugin;
    private final HashMap<String, Manager> managers;
    private final ChatListenerManager chatListenerManager;
    private final SelectorListenerManager selectorListenerManager;
    private final SelPosListenerManager positionListenerManager;
    private final DropListenerManager dropListenerManager;
    private final BlobFileManager blobFileManager;

    /**
     * Constructs a new ManagerDirector.
     *
     * @param plugin The BlobPlugin
     */
    public ManagerDirector(BlobPlugin plugin) {
        this.plugin = plugin;
        this.blobFileManager = new BlobFileManager(this, "plugins/" + plugin.getName());
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
        plugin.registerToBlobLib(this);
    }

    /**
     * Constructs a new ManagerDirector.
     * Here an example if BlobPlugin inside plugin.yml is named 'MyHome':
     * <pre>
     *     ManagerDirector director = new ManagerDirector(plugin, "plugins/MyHome");
     *     </pre>
     *
     * @param plugin              The plugin
     * @param fileManagerPathname The path to the file manager directory.
     *                            Needs to include 'plugins/' since it starts
     *                            from the server's root directory.
     * @deprecated Use {@link #ManagerDirector(BlobPlugin)} instead
     * since it completely automates the process of creating a file manager.
     * Might be removed in the future unless proven useful ???
     */
    @Deprecated
    public ManagerDirector(BlobPlugin plugin, String fileManagerPathname) {
        this.plugin = plugin;
        this.blobFileManager = new BlobFileManager(this, fileManagerPathname);
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
        plugin.registerToBlobLib(this);
    }

    /**
     * Constructs a new manager director by providing a specific BlobFileManager.
     *
     * @param plugin      The plugin
     * @param fileManager The file manager
     */
    public ManagerDirector(BlobPlugin plugin, BlobFileManager fileManager) {
        this.plugin = plugin;
        this.blobFileManager = fileManager;
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
        plugin.registerToBlobLib(this);
    }

    /**
     * Adds a manager to the director.
     *
     * @param key     The key of the manager
     * @param manager The manager
     */
    public void addManager(String key, Manager manager) {
        managers.put(key, manager);
    }

    /**
     * Adds an object director to the director.
     *
     * @param objectName              The name of the object
     * @param readFunction            The function that reads the file
     * @param hasObjectBuilderManager Whether the object director will implement object builder manager
     * @param <T>                     The type of the object
     */
    public <T extends BlobObject> void addDirector(String objectName,
                                                   Function<File, T> readFunction,
                                                   boolean hasObjectBuilderManager) {
        ObjectDirectorData quickWarpData =
                ObjectDirectorData.simple(getFileManager(), objectName);
        addManager(objectName + "Director",
                new ObjectDirector<>(this,
                        quickWarpData, readFunction, hasObjectBuilderManager));
    }

    /**
     * Adds a currency director to the director.
     *
     * @param objectName The name of the object
     */
    public void addCurrencyDirector(String objectName) {
        addManager(objectName + "Director",
                EconomyFactory.CURRENCY_DIRECTOR(this, objectName));
    }

    /**
     * Adds a wallet owner manager to the director.
     *
     * @param key          The key of the manager
     * @param newBorn      A function that by passing a UUID, it will fill a BlobCrudable
     *                     with default key-value pairs.
     *                     This is used to create new/fresh WalletOwners.
     * @param walletOwner  A function that by passing a BlobCrudable, it will return a WalletOwner.
     *                     WalletOwners use this to store their data inside databases.
     * @param crudableName The name of the BlobCrudable. This will be used for
     *                     as the column name in the database.
     * @param logActivity  Whether to log activity in the console.
     * @param joinEvent    A function that by passing a WalletOwner, it will return a join event.
     *                     It's called SYNCHRONOUSLY.
     *                     It's called when a player joins the server.
     * @param quitEvent    A function that by passing a WalletOwner, it will return a quit event.
     *                     It's called SYNCHRONOUSLY.
     *                     It's called when a player quits/leaves the server.
     * @param <T>          The type of WalletOwner.
     */
    public <T extends WalletOwner> void addWalletOwnerManager(String key,
                                                              Function<BlobCrudable, BlobCrudable> newBorn,
                                                              Function<BlobCrudable, T> walletOwner,
                                                              String crudableName, boolean logActivity,
                                                              Function<T, Event> joinEvent,
                                                              Function<T, Event> quitEvent) {
        addManager(key,
                EconomyFactory.WALLET_OWNER_MANAGER(this,
                        newBorn, walletOwner, crudableName, logActivity, joinEvent, quitEvent));
    }

    /**
     * Adds a wallet owner manager to the director.
     * This is a simplified version {@link EconomyFactory#SIMPLE_WALLET_OWNER_MANAGER(ManagerDirector, Function, Function, String, boolean)}
     * No events are registered for join and quit actions.
     *
     * @param key          The key of the manager
     * @param newBorn      A function that by passing a UUID, it will fill a BlobCrudable
     *                     with default key-value pairs.
     *                     This is used to create new/fresh WalletOwners.
     * @param walletOwner  A function that by passing a BlobCrudable, it will return a WalletOwner.
     *                     WalletOwners use this to store their data inside databases.
     * @param crudableName The name of the BlobCrudable. This will be used for
     *                     as the column name in the database.
     * @param logActivity  Whether to log activity in the console.
     * @param <T>          The type of WalletOwner.
     */
    public <T extends WalletOwner> void addSimpleWalletOwnerManager(String key,
                                                                    Function<BlobCrudable, BlobCrudable> newBorn,
                                                                    Function<BlobCrudable, T> walletOwner,
                                                                    String crudableName, boolean logActivity) {
        addManager(key,
                EconomyFactory.SIMPLE_WALLET_OWNER_MANAGER(this,
                        newBorn, walletOwner, crudableName, logActivity));
    }

    /**
     * Adds an object director to the director.
     * This method assumes that the object director will implement object builder manager.
     *
     * @param objectName   The name of the object
     * @param readFunction The function that reads the file
     * @param <T>          The type of the object
     */
    public <T extends BlobObject> void addDirector(String objectName,
                                                   Function<File, T> readFunction) {
        addDirector(objectName, readFunction, true);
    }

    /**
     * Logic that should run whenever reloading the plugin.
     */
    public void reload() {
    }

    /**
     * Will retrieve a manager by providing a String 'key'.
     *
     * @param key the key of the manager
     * @return The Manager that corresponds to the key
     */
    public Manager getManager(String key) {
        return managers.get(key);
    }

    /**
     * Will retrieve a manager by providing a String 'key'.
     *
     * @param key   the key of the manager
     * @param clazz The class of the object
     * @param <T>   The type of the object
     * @return The Manager that corresponds to the key
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(String key, Class<T> clazz) {
        return (T) getManager(key);
    }

    /**
     * Will retrieve an object director by providing a String 'key'.
     * The key must end with 'Director'
     *
     * @param key   the key of the manager
     * @param clazz The class of the object
     * @param <T>   The type of the object
     * @return The ObjectDirector that corresponds to the key
     */
    @SuppressWarnings("unchecked")
    public <T extends BlobObject> ObjectDirector<T> getDirector(String key,
                                                                Class<T> clazz) {
        return (ObjectDirector<T>) getManager(key + "Director");
    }

    /**
     * Will retrieve a wallet owner manager by providing a String 'key'.
     * The key must end with 'Manager'
     *
     * @param key   the key of the manager
     * @param clazz The class of the object
     * @param <T>   The type of the object
     * @return The WalletOwnerManager that corresponds to the key
     */
    @SuppressWarnings("unchecked")
    public <T extends WalletOwner> WalletOwnerManager<T> getWalletOwnerManager(String key,
                                                                               Class<T> clazz) {
        return (WalletOwnerManager<T>) getManager(key);
    }

    /**
     * Will retrieve a currency director by providing a String 'objectName'.
     *
     * @param objectName The name of the object
     * @return The ObjectDirector that corresponds to the key
     */
    public ObjectDirector<Currency> getCurrencyDirector(String objectName) {
        return getDirector(objectName, Currency.class);
    }

    /**
     * Logic that should run when plugin is unloading.
     */
    public void unload() {
    }

    /**
     * Logic that should run after the world has been loaded.
     */
    public void postWorld() {
    }

    /**
     * @return The ChatListenerManager that the director manages.
     */
    public ChatListenerManager getChatListenerManager() {
        return chatListenerManager;
    }

    /**
     * @return The SelectorListenerManager that the director manages.
     */
    public SelectorListenerManager getSelectorManager() {
        return selectorListenerManager;
    }

    /**
     * @return The SelectPositionListenerManager that the director manages.
     */
    public SelPosListenerManager getPositionListenerManager() {
        return positionListenerManager;
    }

    /**
     * @return The DropListenerManager that the director manages.
     */
    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    /**
     * @return The BlobFileManager that the director manages.
     */
    public BlobFileManager getFileManager() {
        return blobFileManager;
    }

    /**
     * @return The BlobPlugin that this director belongs.
     */
    public BlobPlugin getPlugin() {
        return plugin;
    }

    /**
     * Will detach an embedded BlobMessage file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     *
     * @param fileName The name of the file to detach
     * @param debug    Whether to print debug messages
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector detachMessageAsset(String fileName, boolean debug) {
        Logger logger = getPlugin().getAnjoLogger();
        File path = getFileManager().messagesDirectory();
        File file = new File(path + "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                if (debug)
                    logger.debug(" message asset " + fileName + ".yml was not detached");
                e.printStackTrace();
                return this;
            }
        }
        ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
        boolean successful = MessageManager.loadAndRegisterYamlConfiguration(file, plugin);
        if (debug && successful)
            logger.debug(" message asset " + fileName + ".yml successfully detached");
        return this;
    }

    /**
     * Will detach an embedded BlobMessage file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     * Will not print debug messages.
     *
     * @param fileName The name of the file to detach
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector detachMessageAsset(String fileName) {
        return detachMessageAsset(fileName, false);
    }

    /**
     * Will detach an embedded BlobSound file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     *
     * @param fileName The name of the file to detach
     * @param debug    Whether to print debug messages
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector detachSoundAsset(String fileName, boolean debug) {
        Logger logger = getPlugin().getAnjoLogger();
        File path = getFileManager().soundsDirectory();
        File file = new File(path + "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                if (debug)
                    logger.debug(" sound asset " + fileName + ".yml was not detached");
                e.printStackTrace();
                return this;
            }
        }
        ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
        SoundManager.loadAndRegisterYamlConfiguration(plugin, file);
        if (debug)
            logger.debug(" sound asset " + fileName + ".yml successfully detached");
        return this;
    }

    /**
     * Will detach an embedded BlobSound file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     * Will not print debug messages.
     *
     * @param fileName The name of the file to detach
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector detachSoundAsset(String fileName) {
        return detachSoundAsset(fileName, false);
    }

    /**
     * Will detach an embedded Inventory file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     *
     * @param fileName The name of the file to detach
     * @param debug    Whether to print debug messages
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerAndUpdateBlobInventory(String fileName, boolean debug) {
        File path = getFileManager().inventoriesDirectory();
        File file = new File(path + "/" + fileName + ".yml");
        if (!blobFileManager.updateYAML(file))
            return this;
        InventoryManager.continueLoadingBlobInventories(plugin, file);
        if (debug)
            getPlugin().getAnjoLogger().debug(" inventory asset " + fileName + ".yml successfully registered");
        return this;
    }

    /**
     * Will detach an embedded Inventory file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     * Will not print debug messages.
     *
     * @param fileName The name of the file to detach
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerAndUpdateBlobInventory(String fileName) {
        return registerAndUpdateBlobInventory(fileName, false);
    }

    /**
     * Will detach an embedded Inventory file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     *
     * @param fileName The name of the file to detach
     * @param debug    Whether to print debug messages
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerAndUpdateMetaBlobInventory(String fileName, boolean debug) {
        File path = getFileManager().metaInventoriesDirectory();
        File file = new File(path + "/" + fileName + ".yml");
        if (!blobFileManager.updateYAML(file))
            return this;
        InventoryManager.continueLoadingMetaInventories(plugin, file);
        if (debug)
            getPlugin().getAnjoLogger().debug(" inventory asset " + fileName + ".yml successfully registered");
        return this;
    }

    /**
     * Will detach an embedded Inventory file/asset from the plugin jar to
     * the corresponding directory in the plugin data folder.
     * Will not print debug messages.
     *
     * @param fileName The name of the file to detach
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerAndUpdateMetaBlobInventory(String fileName) {
        return registerAndUpdateMetaBlobInventory(fileName, false);
    }

    protected Set<Map.Entry<String, Manager>> getManagerEntry() {
        return managers.entrySet();
    }
}