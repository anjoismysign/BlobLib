package us.mytheria.bloblib.managers;

import me.anjoismysign.anjo.logger.Logger;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.*;
import us.mytheria.bloblib.entities.currency.Currency;
import us.mytheria.bloblib.entities.currency.EconomyFactory;
import us.mytheria.bloblib.entities.currency.WalletOwner;
import us.mytheria.bloblib.entities.currency.WalletOwnerManager;
import us.mytheria.bloblib.entities.proxy.BlobProxifier;
import us.mytheria.bloblib.exception.KeySharingException;
import us.mytheria.bloblib.utilities.HandyDirectory;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ManagerDirector implements IManagerDirector {
    private final BlobPlugin plugin;
    private final HashMap<String, Manager> managers;
    private final ChatListenerManager chatListenerManager;
    private final SelectorListenerManager selectorListenerManager;
    private final SelPosListenerManager positionListenerManager;
    private final DropListenerManager dropListenerManager;
    private final BlobFileManager blobFileManager;
    private final BukkitPluginOperator pluginOperator;
    private final IFileManager proxiedFileManager;
    private final Map<String, NamespacedKey> namespacedKeys;

    /**
     * Constructs a new ManagerDirector.
     *
     * @param plugin The BlobPlugin
     */
    public ManagerDirector(BlobPlugin plugin) {
        this.namespacedKeys = new HashMap<>();
        this.plugin = plugin;
        reloadNamespacedKeys();
        this.pluginOperator = () -> plugin;
        this.blobFileManager = new BlobFileManager(this,
                "plugins/" + plugin.getName(),
                plugin);
        this.proxiedFileManager = BlobProxifier.PROXY(blobFileManager);
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
     * @param fileManager The fileManager
     */
    public ManagerDirector(BlobPlugin plugin, BlobFileManager fileManager) {
        this.namespacedKeys = new HashMap<>();
        this.plugin = plugin;
        reloadNamespacedKeys();
        this.pluginOperator = () -> plugin;
        this.blobFileManager = Objects.requireNonNull(fileManager, "BlobFileManager cannot be null!");
        this.proxiedFileManager = BlobProxifier.PROXY(blobFileManager);
        chatListenerManager = BlobLib.getInstance().getChatManager();
        selectorListenerManager = BlobLib.getInstance().getSelectorManager();
        positionListenerManager = BlobLib.getInstance().getPositionManager();
        dropListenerManager = BlobLib.getInstance().getDropListenerManager();
        managers = new HashMap<>();
        plugin.registerToBlobLib(this);
    }

    public NamespacedKey getNamespacedKey(String key) {
        return namespacedKeys.get(key);
    }

    /**
     * Will proxy this ManagerDirector to a new instance of ManagerDirector.
     *
     * @return The proxied ManagerDirector
     */
    public IManagerDirector proxy() {
        return BlobProxifier.PROXY(this);
    }

    /**
     * Will get the BukkitPluginOperator of the plugin.
     *
     * @return The BukkitPluginOperator
     */
    public BukkitPluginOperator getPluginOperator() {
        return pluginOperator;
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
        ObjectDirectorData directorData =
                ObjectDirectorData.simple(getRealFileManager(), objectName);
        addManager(objectName + "Director",
                new ObjectDirector<>(this,
                        directorData, readFunction, hasObjectBuilderManager));
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
     * Adds a BlobSerializableManager to the director
     * that does not listen to events.
     * Uses NORMAL priority for join and quit listeners.
     *
     * @param key          The key of the manager
     * @param generator    The generator function
     * @param crudableName The name of the crudable
     * @param logActivity  Whether to log activity
     * @param <T>          The type of the BlobSerializable
     */
    public <T extends BlobSerializable> void addSimpleBlobSerializableManager(String key,
                                                                              Function<BlobCrudable, T> generator,
                                                                              String crudableName,
                                                                              boolean logActivity) {
        addManager(key, BlobSerializableManagerFactory.SIMPLE(this,
                generator, crudableName, logActivity));
    }

    /**
     * Adds a BlobSerializableManager to the director
     * that listens to events.
     *
     * @param key          The key of the manager
     * @param generator    The generator function
     * @param crudableName The name of the crudable
     * @param logActivity  Whether to log activity
     * @param joinEvent    The join event.
     *                     Function consumes the BlobSerializable
     *                     related in the event and needs to return
     *                     the event to be called.
     * @param quitEvent    The quit event.
     *                     Function consumes the BlobSerializable
     *                     related in the event and needs to return
     *                     the event to be called.
     * @param <T>          The type of the blob serializable
     */
    public <T extends BlobSerializable> void addListenerBlobSerializableManager(String key,
                                                                                Function<BlobCrudable, T> generator,
                                                                                String crudableName,
                                                                                boolean logActivity,
                                                                                @Nullable Function<T, Event> joinEvent,
                                                                                @Nullable Function<T, Event> quitEvent,
                                                                                @NotNull EventPriority joinPriority,
                                                                                @NotNull EventPriority quitPriority) {
        addManager(key, BlobSerializableManagerFactory.LISTENER(this,
                generator, crudableName, logActivity, joinEvent, quitEvent, joinPriority, quitPriority));
    }

    /**
     * Adds a BlobSerializableManager to the director
     * that listens to events.
     * Uses NORMAL priority for join and quit listeners.
     *
     * @param key          The key of the manager
     * @param generator    The generator function
     * @param crudableName The name of the crudable
     * @param logActivity  Whether to log activity
     * @param joinEvent    The join event.
     *                     Function consumes the BlobSerializable
     *                     related in the event and needs to return
     *                     the event to be called.
     * @param quitEvent    The quit event.
     *                     Function consumes the BlobSerializable
     *                     related in the event and needs to return
     *                     the event to be called.
     * @param <T>          The type of the blob serializable
     */
    public <T extends BlobSerializable> void addListenerBlobSerializableManager(String key,
                                                                                Function<BlobCrudable, T> generator,
                                                                                String crudableName,
                                                                                boolean logActivity,
                                                                                @Nullable Function<T, Event> joinEvent,
                                                                                @Nullable Function<T, Event> quitEvent) {
        addListenerBlobSerializableManager(key, generator, crudableName, logActivity,
                joinEvent, quitEvent, EventPriority.NORMAL, EventPriority.NORMAL);
    }

    /**
     * Will instantiate a BlobPHExpansion and still allow calling
     * methods on it.
     *
     * @param identifier The identifier of the expansion
     * @param consumer   The consumer that will consume the expansion
     */
    public void instantiateBlobPHExpansion(String identifier, Consumer<BlobPHExpansion> consumer) {
        if (!isPlaceholderAPIEnabled()) {
            getPlugin().getAnjoLogger().log("PlaceholderAPI not found, not instantiating PlaceholderAPI expansion for " + getPlugin().getName());
            return;
        }
        BlobPHExpansion expansion = new BlobPHExpansion(getPlugin(), identifier);
        consumer.accept(expansion);
    }

    /**
     * Checks whether PlaceholderAPI is enabled.
     *
     * @return Whether PlaceholderAPI is enabled.
     */
    public boolean isPlaceholderAPIEnabled() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
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
     * @param joinPriority The priority of the join event.
     * @param quitPriority The priority of the quit event.
     * @param <T>          The type of WalletOwner.
     */
    public <T extends WalletOwner> void addWalletOwnerManager(String key,
                                                              Function<BlobCrudable, BlobCrudable> newBorn,
                                                              Function<BlobCrudable, T> walletOwner,
                                                              String crudableName, boolean logActivity,
                                                              @Nullable Function<T, Event> joinEvent,
                                                              @Nullable Function<T, Event> quitEvent,
                                                              @NotNull EventPriority joinPriority,
                                                              @NotNull EventPriority quitPriority) {
        addManager(key,
                EconomyFactory.WALLET_OWNER_MANAGER(this,
                        newBorn, walletOwner, crudableName, logActivity, joinEvent, quitEvent,
                        joinPriority, quitPriority));
    }

    /**
     * Adds a wallet owner manager to the director.
     * Uses NORMAL priority for join and quit listeners.
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
                                                              @Nullable Function<T, Event> joinEvent,
                                                              @Nullable Function<T, Event> quitEvent) {
        addWalletOwnerManager(key, newBorn, walletOwner, crudableName, logActivity,
                joinEvent, quitEvent, EventPriority.NORMAL, EventPriority.NORMAL);
    }

    /**
     * Adds a wallet owner manager to the director.
     * This is a simplified version {@link EconomyFactory#SIMPLE_WALLET_OWNER_MANAGER(ManagerDirector, Function, Function, String, boolean)}
     * No events are registered for join and quit actions.
     * Uses NORMAL priority for join and quit listeners.
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
     * Will reload ManagerDirector's defaults and do reload logic.
     */
    public void reloadAll() {
        reloadNamespacedKeys();
        reload();
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
     *
     * @param key   the key of the manager
     * @param clazz The class of the object
     * @param <T>   The type of the object (needs to implement WalletOwner)
     * @return The WalletOwnerManager that corresponds to the key
     */
    @SuppressWarnings("unchecked")
    public <T extends WalletOwner> WalletOwnerManager<T> getWalletOwnerManager(String key,
                                                                               Class<T> clazz) {
        return (WalletOwnerManager<T>) getManager(key);
    }

    /**
     * Will retrieve a blob serializable manager by providing a String 'key'.
     *
     * @param key   the key of the manager
     * @param clazz The class of the object
     * @param <T>   The type of the object (needs to implement BlobSerializable)
     * @return The BlobSerializableManager that corresponds to the key
     */
    @SuppressWarnings("unchecked")
    public <T extends BlobSerializable> BlobSerializableManager<T> getBlobSerializableManager(String key, Class<T> clazz) {
        return (BlobSerializableManager<T>) getManager(key);
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

    public IFileManager getFileManager() {
        return proxiedFileManager;
    }

    /**
     * @return The BlobFileManager that the director manages.
     */
    public BlobFileManager getRealFileManager() {
        return blobFileManager;
    }

    /**
     * @return The BlobPlugin that this director belongs.
     */
    public BlobPlugin getPlugin() {
        return plugin;
    }

    /**
     * Will detach an embedded file/asset from the plugin jar to
     * the corresponding directory.
     * If file extension is '.yml', it won't replace the file if it already exists.
     * If file does not exist, it will create it and then copy the
     * embedded file/asset to it.
     * Allows custom output directory based on fileName.
     * "detachAsset("es_es/lang.yml", false, new File("plugins"))"
     * Would detach the file to "plugins/es_es/lang.yml".
     *
     * @param fileName The name of the file to detach. Needs to include the extension.
     * @param debug    Whether to print debug messages
     * @param path     The path to the directory
     * @return The ManagerDirector instance for method chaining
     */
    public FileDetachment detachAsset(String fileName, boolean debug, File path) {
        Logger logger = getPlugin().getAnjoLogger();
        String[] split = fileName.split("/");
        String original = fileName;
        if (split.length > 1) {
            String end = split[split.length - 1];
            path = new File(path + "/" + fileName.replace(end, ""));
            fileName = end;
        }
        File file = new File(path + "/" + fileName);
        boolean successful = false;
        String extension = FilenameUtils.getExtension(fileName);
        boolean isFresh = !file.exists();
        if (extension.equals("yml")) {
            blobFileManager.updateYAML(file, original);
        } else {
            if (!file.exists())
                successful = true;
            ResourceUtil.moveResource(file, plugin.getResource(original));
        }
        if (debug && successful)
            logger.debug(" asset " + original + " successfully detached");
        return new FileDetachment(file, isFresh);
    }

    private String[] addYml(String... fileNames) {
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = fileNames[i] + ".yml";
        }
        return fileNames;
    }

    private File[] freshFiles(boolean debug, File path, String... fileNames) {
        List<File> files = new ArrayList<>();
        for (String fileName : fileNames) {
            FileDetachment detachment = detachAsset(fileName, debug, path);
            if (detachment.isFresh())
                files.add(detachment.file());
        }
        return files.toArray(new File[0]);
    }

    /**
     * Will detach all MetaInventories provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerMetaBlobInventory(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().metaInventoriesDirectory(), yaml);
        InventoryManager.continueLoadingMetaInventories(plugin, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" inventory asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all MetaInventories provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerMetaBlobInventory(String... fileNames) {
        return registerMetaBlobInventory(false, fileNames);
    }

    /**
     * Will detach all BlobInventories provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobInventory(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().inventoriesDirectory(), yaml);
        InventoryManager.continueLoadingBlobInventories(plugin, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" inventory asset " + Arrays.toString(fileNames) + ".yml successfully registered");
        return this;
    }

    /**
     * Will detach all BlobInventories provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobInventory(String... fileNames) {
        return registerBlobInventory(false, fileNames);
    }

    /**
     * Will detach all BlobMessages provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobMessage(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().messagesDirectory(), yaml);
        MessageManager.continueLoadingMessages(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" message asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all BlobMessages provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobMessage(String... fileNames) {
        return registerBlobMessage(false, fileNames);
    }

    /**
     * Will detach all BlobSounds provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobSound(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().soundsDirectory(), yaml);
        SoundManager.continueLoadingSounds(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" sound asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all BlobSounds provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerBlobSound(String... fileNames) {
        return registerBlobSound(false, fileNames);
    }

    /**
     * Will detach all TranslatableBlocks provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableBlock(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().translatableBlocksDirectory(), yaml);
        TranslatableManager.continueLoadingBlocks(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" translatable block asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all TranslatableBlocks provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableBlock(String... fileNames) {
        return registerTranslatableBlock(false, fileNames);
    }

    /**
     * Will detach all TranslatableSnippets provided.
     * If they already exist, they won't be replaced.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableSnippet(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().translatableSnippetsDirectory(), yaml);
        TranslatableManager.continueLoadingSnippets(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" translatable block asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all TranslatableSnippets provided.
     * If they already exist, they won't be replaced.
     * It won't print debug messages.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableSnippet(String... fileNames) {
        return registerTranslatableSnippet(false, fileNames);
    }

    /**
     * Will detach all TranslatableItems provided.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableItem(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().getDirectory(DataAssetType.TRANSLATABLE_ITEM), yaml);
        BlobLib.getInstance().getTranslatableItemManager().continueLoadingAssets(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" translatable item asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all TranslatableItems provided.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTranslatableItem(String... fileNames) {
        return registerTranslatableItem(false, fileNames);
    }

    /**
     * Will detach all TagSets provided.
     *
     * @param debug     Whether to print debug messages
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTagSet(boolean debug, String... fileNames) {
        String[] yaml = addYml(fileNames);
        File[] freshFiles = freshFiles(debug, getRealFileManager().getDirectory(DataAssetType.TAG_SET), yaml);
        BlobLib.getInstance().getTagSetManager().continueLoadingAssets(plugin, true, freshFiles);
        if (debug)
            getPlugin().getAnjoLogger().debug(" tag set asset " + Arrays.toString(fileNames) + " successfully registered");
        return this;
    }

    /**
     * Will detach all TagSets provided.
     *
     * @param fileNames The names of the files to detach. Needs to include the extension.
     * @return The ManagerDirector instance for method chaining
     */
    public ManagerDirector registerTagSet(String... fileNames) {
        return registerTagSet(false, fileNames);
    }

    /**
     * Creates a new NamespacedKey.
     *
     * @param key The key
     * @return The NamespacedKey
     */
    public NamespacedKey createNamespacedKey(String key) {
        if (namespacedKeys.containsKey(key))
            throw KeySharingException.DEFAULT(key);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        namespacedKeys.put(key, namespacedKey);
        return namespacedKey;
    }

    /**
     * Reloads all namespaced keys.
     */
    public void reloadNamespacedKeys() {
        namespacedKeys.clear();
        createNamespacedKey("tangibleCurrencyKey");
        createNamespacedKey("tangibleCurrencyDenomination");
    }

    /**
     * Loads a BlobLib expansion.
     * It will unzip it and load all assets.
     * All assets that cannot be loaded (because it's either an Action or not a BlobLib asset),
     * will stay in the output directory.
     *
     * @param expansion The expansion file
     * @return Whether the expansion was successfully loaded
     */
    public boolean loadBlobLibExpansion(@NotNull File expansion) {
        Objects.requireNonNull(expansion);
        File expansionDirectory = new File(plugin.getDataFolder(), "expansion");
        if (!expansion.getPath().startsWith(expansionDirectory.getPath()))
            throw new IllegalArgumentException("Expansion must be in '" + expansionDirectory.getPath() + "' directory," +
                    " but was in '" + expansion.getPath() + "'.");
        File outputFile = new File(expansionDirectory, "output");
        if (!outputFile.exists())
            outputFile.mkdirs();
        try (ZipFile zipFile = new ZipFile(expansion)) {
            zipFile.extractAll(outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        HandyDirectory handyDirectory = HandyDirectory.of(outputFile);
        Map<DataAssetType, List<File>> assets = new HashMap<>();
        Map<DataAssetType, File> assetsDirectory = new HashMap<>();
        for (File directory : handyDirectory.listDirectories()) {
            if (directory.getName().equalsIgnoreCase(DataAssetType.BLOB_SOUND.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.BLOB_SOUND, list);
                assetsDirectory.put(DataAssetType.BLOB_SOUND, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.BLOB_MESSAGE.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.BLOB_MESSAGE, list);
                assetsDirectory.put(DataAssetType.BLOB_MESSAGE, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.BLOB_INVENTORY.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.BLOB_INVENTORY, list);
                assetsDirectory.put(DataAssetType.BLOB_INVENTORY, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.META_BLOB_INVENTORY.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.META_BLOB_INVENTORY, list);
                assetsDirectory.put(DataAssetType.META_BLOB_INVENTORY, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.TRANSLATABLE_BLOCK.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.TRANSLATABLE_BLOCK, list);
                assetsDirectory.put(DataAssetType.TRANSLATABLE_BLOCK, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.TRANSLATABLE_SNIPPET.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.TRANSLATABLE_SNIPPET, list);
                assetsDirectory.put(DataAssetType.TRANSLATABLE_SNIPPET, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.TRANSLATABLE_ITEM.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.TRANSLATABLE_ITEM, list);
                assetsDirectory.put(DataAssetType.TRANSLATABLE_ITEM, directory);
                continue;
            }
            if (directory.getName().equalsIgnoreCase(DataAssetType.TAG_SET.getObjectName())) {
                HandyDirectory subDirectory = HandyDirectory.of(directory);
                List<File> list = subDirectory.listRecursively("yml").stream().toList();
                assets.put(DataAssetType.TAG_SET, list);
                assetsDirectory.put(DataAssetType.TAG_SET, directory);
            }
        }
        if (assets.isEmpty())
            return true;
        BlobLib blobLib = BlobLib.getInstance();
        List<File> blobSound = assets.get(DataAssetType.BLOB_SOUND);
        if (blobSound != null && !blobSound.isEmpty()) {
            SoundManager.continueLoadingSounds(plugin, true, blobSound.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.BLOB_SOUND));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> blobMessage = assets.get(DataAssetType.BLOB_MESSAGE);
        if (blobMessage != null && !blobMessage.isEmpty()) {
            MessageManager.continueLoadingMessages(plugin, true, blobMessage.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.BLOB_MESSAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> blobInventory = assets.get(DataAssetType.BLOB_INVENTORY);
        if (blobInventory != null && !blobInventory.isEmpty()) {
            InventoryManager.continueLoadingBlobInventories(plugin, blobInventory.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.BLOB_INVENTORY));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> metaBlobInventory = assets.get(DataAssetType.META_BLOB_INVENTORY);
        if (metaBlobInventory != null && !metaBlobInventory.isEmpty()) {
            InventoryManager.continueLoadingMetaInventories(plugin, metaBlobInventory.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.META_BLOB_INVENTORY));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> translatableBlock = assets.get(DataAssetType.TRANSLATABLE_BLOCK);
        if (translatableBlock != null
                && !translatableBlock.isEmpty()) {
            TranslatableManager.continueLoadingBlocks(plugin, true, translatableBlock.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.TRANSLATABLE_BLOCK));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> translatableSnippet = assets.get(DataAssetType.TRANSLATABLE_SNIPPET);
        if (translatableSnippet != null && !translatableSnippet.isEmpty()) {
            TranslatableManager.continueLoadingSnippets(plugin, true, translatableSnippet.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.TRANSLATABLE_SNIPPET));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> translatableItem = assets.get(DataAssetType.TRANSLATABLE_ITEM);
        if (translatableItem != null && !translatableItem.isEmpty()) {
            blobLib.getTranslatableItemManager().continueLoadingAssets(plugin, true, translatableItem.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.TRANSLATABLE_ITEM));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<File> tagSet = assets.get(DataAssetType.TAG_SET);
        if (tagSet != null && !tagSet.isEmpty()) {
            blobLib.getTagSetManager().continueLoadingAssets(plugin, true, tagSet.toArray(new File[0]));
            try {
                FileUtils.deleteDirectory(assetsDirectory.get(DataAssetType.TAG_SET));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // a ManagerDirector can override this method and do their own logic
    public boolean isReloading() {
        return false;
    }

    protected Set<Map.Entry<String, Manager>> getManagerEntry() {
        return managers.entrySet();
    }
}