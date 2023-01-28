package us.mytheria.bloblib.entities.manager;

import me.anjoismysign.anjo.logger.Logger;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobFileManager;
import us.mytheria.bloblib.managers.*;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
    }

    public void addManager(String key, Manager manager) {
        managers.put(key, manager);
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
                e.printStackTrace();
                logger.debug(" message asset " + fileName + ".yml was not detached");
                return this;
            }
        }
        ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
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
                e.printStackTrace();
                logger.debug(" sound asset " + fileName + ".yml was not detached");
                return this;
            }
        }
        ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
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
    public ManagerDirector detachInventoryAsset(String fileName, boolean debug) {
        Logger logger = getPlugin().getAnjoLogger();
        File path = getFileManager().inventoriesDirectory();
        File file = new File(path + "/" + fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug(" inventory asset " + fileName + ".yml was not detached");
                return this;
            }
        }
        ResourceUtil.updateYml(path, "/temp" + fileName + ".yml", fileName + ".yml", file, plugin);
        logger.debug(" inventory asset " + fileName + ".yml successfully detached");
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
    public ManagerDirector detachInventoryAsset(String fileName) {
        return detachInventoryAsset(fileName, false);
    }
}