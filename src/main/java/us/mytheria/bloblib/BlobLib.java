package us.mytheria.bloblib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.command.BlobLibCmd;
import us.mytheria.bloblib.disguises.DisguiseManager;
import us.mytheria.bloblib.enginehub.EngineHubManager;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.hologram.HologramManager;
import us.mytheria.bloblib.managers.*;
import us.mytheria.bloblib.managers.fillermanager.FillerManager;
import us.mytheria.bloblib.reflection.BlobReflectionLib;
import us.mytheria.bloblib.utilities.SerializationLib;
import us.mytheria.bloblib.vault.VaultManager;

/**
 * The main class of the plugin
 */
public class BlobLib extends JavaPlugin {
    private static BlobPluginLogger anjoLogger;

    private BlobLibUpdater bloblibupdater;

    private ScriptManager scriptManager;
    private VaultManager vaultManager;
    private EngineHubManager engineHubManager;
    private DisguiseManager disguiseManager;
    private HologramManager hologramManager;
    private BlobLibFileManager fileManager;
    private InventoryManager inventoryManager;
    private MessageManager messageManager;
    private SoundManager soundManager;
    private FillerManager fillerManager;
    private ChatListenerManager chatManager;
    private SelPosListenerManager positionManager;
    private SelectorListenerManager selectorManager;
    private VariableSelectorManager variableSelectorManager;
    private DropListenerManager dropListenerManager;
    private ColorManager colorManager;
    private PluginManager pluginManager;
    private ActionManager actionManager;
    private BlobLibConfigManager configManager;
    private BlobLibListenerManager listenerManager;
    private BlobReflectionLib reflectionLib;
    private SerializationLib serializationLib;

    private static BlobLib instance;

    /**
     * Will retrieve the instance of the plugin
     *
     * @return The instance of the plugin
     */
    public static BlobLib getInstance() {
        return instance;
    }

    /**
     * Will retrieve the Logger implementation of Anjo framework.
     *
     * @return The Logger implementation of Anjo framework.
     */
    public static BlobPluginLogger getAnjoLogger() {
        return anjoLogger;
    }

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        instance = this;
        bloblibupdater = new BlobLibUpdater(this);
        reflectionLib = BlobReflectionLib.getInstance(this);
        serializationLib = SerializationLib.getInstance(this);
        anjoLogger = new BlobPluginLogger(this);
        scriptManager = new ScriptManager();
        pluginManager = new PluginManager();
        colorManager = new ColorManager();
        fileManager = new BlobLibFileManager();
        fileManager.unpackYamlFile("/BlobInventory", "CurrencyBuilder", false);

        inventoryManager = new InventoryManager();
        messageManager = new MessageManager();
        actionManager = new ActionManager();
        soundManager = new SoundManager();
        fillerManager = new FillerManager();
        vaultManager = new VaultManager();
        engineHubManager = new EngineHubManager();
        disguiseManager = new DisguiseManager();
        hologramManager = new HologramManager();
        chatManager = new ChatListenerManager();
        positionManager = new SelPosListenerManager();
        selectorManager = new SelectorListenerManager();
        variableSelectorManager = new VariableSelectorManager();
        dropListenerManager = new DropListenerManager();
        configManager = BlobLibConfigManager.getInstance(this);
        listenerManager = BlobLibListenerManager.getInstance(configManager);

        //Load reloadable managers
        reload();
        new BlobLibCmd();

        Bukkit.getScheduler().runTask(this,
                () -> disguiseManager.load());
    }

    /**
     * Will reload all the managers
     */
    public void reload() {
        configManager.reload();
        listenerManager.reload();
        soundManager.reload();
        messageManager.reload();
        actionManager.reload();
        inventoryManager.reload();
        getPluginManager().reload();
    }

    public BlobLibUpdater getBloblibupdater() {
        return bloblibupdater;
    }

    /**
     * Will retrieve the plugin manager.
     * This manager will handle all the plugins that are using BlobLib.
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Will retrieve the color manager.
     *
     * @return The color manager
     */
    public ColorManager getColorManager() {
        return colorManager;
    }

    /**
     * Will retrieve the ScriptManager
     *
     * @return The ScriptManager
     */
    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    /**
     * Will retrieve the FileManager
     *
     * @return The FileManager
     */
    public BlobLibFileManager getFileManager() {
        return fileManager;
    }

    /**
     * Will retrieve the InventoryManager
     *
     * @return The InventoryManager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    /**
     * Will retrieve the MessageManager
     *
     * @return The MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Will retrieve the ActionManager
     *
     * @return The ActionManager
     */
    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * Will retrieve the SoundManager
     *
     * @return The SoundManager
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Will retrieve the VaultManager
     *
     * @return The VaultManager
     */
    public VaultManager getVaultManager() {
        return vaultManager;
    }

    /**
     * Will retrieve the VaultManager
     *
     * @return The VaultManager
     */
    public static VaultManager vaultManager() {
        return getInstance().getVaultManager();
    }

    /**
     * Will retrieve the HologramManager
     *
     * @return The HologramManager
     */
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    /**
     * Will retrieve the ChatListenerManager
     *
     * @return The ChatListenerManager
     */
    public ChatListenerManager getChatManager() {
        return chatManager;
    }

    /**
     * Will retrieve the SelectPositionListenerManager(SelPosListenerManager)
     *
     * @return The SelPosListenerManager
     */
    public SelPosListenerManager getPositionManager() {
        return positionManager;
    }

    /**
     * Will retrieve the SelectorListenerManager
     *
     * @return The SelectorListenerManager
     */
    public SelectorListenerManager getSelectorManager() {
        return selectorManager;
    }

    /**
     * Will retrieve the VariableSelectorManager
     *
     * @return The VariableSelectorManager
     */
    public VariableSelectorManager getVariableSelectorManager() {
        return variableSelectorManager;
    }

    /**
     * Will retrieve the FillerManager
     *
     * @return The FillerManager
     */
    public FillerManager getFillerManager() {
        return fillerManager;
    }

    /**
     * Will retrieve the DropListenerManager
     *
     * @return The DropListenerManager
     */
    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    /**
     * Will retrieve the EngineHubManager
     *
     * @return The EngineHubManager
     */
    public EngineHubManager getEngineHubManager() {
        return engineHubManager;
    }

    protected DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }
}
