package us.mytheria.bloblib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.command.BlobLibCmd;
import us.mytheria.bloblib.disguises.DisguiseManager;
import us.mytheria.bloblib.enginehub.EngineHubManager;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.entities.area.WorldGuardArea;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.entities.positionable.Positionable;
import us.mytheria.bloblib.entities.positionable.PositionableReader;
import us.mytheria.bloblib.entities.tag.TagSet;
import us.mytheria.bloblib.entities.tag.TagSetReader;
import us.mytheria.bloblib.entities.translatable.*;
import us.mytheria.bloblib.exception.ConfigurationFieldException;
import us.mytheria.bloblib.hologram.HologramManager;
import us.mytheria.bloblib.managers.*;
import us.mytheria.bloblib.managers.fillermanager.FillerManager;
import us.mytheria.bloblib.placeholderapi.TranslatablePH;
import us.mytheria.bloblib.placeholderapi.WorldGuardPH;
import us.mytheria.bloblib.utilities.MinecraftVersion;
import us.mytheria.bloblib.utilities.SerializationLib;
import us.mytheria.bloblib.vault.VaultManager;

/**
 * The main class of the plugin
 */
public class BlobLib extends JavaPlugin {
    private static BlobPluginLogger anjoLogger;

    private BlobLibUpdater bloblibupdater;
    private BlobLibAPI api;

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
    private SerializationLib serializationLib;
    private InventoryTrackerManager inventoryTrackerManager;
    private TranslatableManager translatableManager;

    private LocalizableDataAssetManager<TranslatableItem> translatableItemManager;
    private LocalizableDataAssetManager<TranslatablePositionable> translatablePositionableManager;
    private LocalizableDataAssetManager<TranslatableArea> translatableAreaManager;
    private DataAssetManager<TagSet> tagSetManager;

    private MinecraftVersion running;
    private SoulAPI soulAPI;
    private UniqueAPI uniqueAPI;

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
        running = MinecraftVersion.getRunning();
        soulAPI = SoulAPI.getInstance(this);
        uniqueAPI = UniqueAPI.getInstance(this);
        api = BlobLibAPI.getInstance(this);
        bloblibupdater = new BlobLibUpdater(this);
        serializationLib = SerializationLib.getInstance(this);
        anjoLogger = new BlobPluginLogger(this);
        scriptManager = new ScriptManager();
        pluginManager = new PluginManager();
        colorManager = new ColorManager();
        fileManager = new BlobLibFileManager();
        fileManager.unpackYamlFile("/BlobInventory", "CurrencyBuilder", false);
        engineHubManager = EngineHubManager.getInstance();

        inventoryManager = new InventoryManager();
        inventoryTrackerManager = new InventoryTrackerManager();
        translatableManager = new TranslatableManager();
        tagSetManager = DataAssetManager.of(fileManager.getDirectory(DataAssetType.TAG_SET),
                TagSetReader::READ,
                DataAssetType.TAG_SET,
                section -> section.isList("Inclusions") ||
                        !section.getStringList("Include-Set").isEmpty());
        translatableItemManager = LocalizableDataAssetManager
                .of(fileManager.getDirectory(DataAssetType.TRANSLATABLE_ITEM),
                        TranslatableReader::ITEM,
                        DataAssetType.TRANSLATABLE_ITEM,
                        section -> section.isConfigurationSection("ItemStack"));
        translatablePositionableManager = LocalizableDataAssetManager
                .of(fileManager.getDirectory(DataAssetType.TRANSLATABLE_POSITIONABLE),
                        (section, locale, key) -> {
                            Positionable positionable = PositionableReader.getInstance().read(section);
                            if (!section.isString("Display"))
                                throw new ConfigurationFieldException("'Display' is missing or not set");
                            String display = section.getString("Display");
                            return BlobTranslatablePositionable.of(key, locale, display, positionable);
                        },
                        DataAssetType.TRANSLATABLE_POSITIONABLE,
                        section -> section.isDouble("X") && section.isDouble("Y") && section.isDouble("Z"));
        translatableAreaManager = LocalizableDataAssetManager
                .of(fileManager.getDirectory(DataAssetType.TRANSLATABLE_AREA),
                        (section, locale, key) -> {
                            if (!engineHubManager.isWorldGuardInstalled()) {
                                getLogger().severe("WorldGuard is not installed, failed to load Area: " + key);
                                return null;
                            }
                            String worldName = section.getString("World");
                            String id = section.getString("Id");
                            String display = section.getString("Display");
                            return BlobTranslatableArea.of(key, locale, display, WorldGuardArea.of(worldName, id));
                        },
                        DataAssetType.TRANSLATABLE_AREA,
                        section -> section.isString("World") && section.isString("Id"));
        messageManager = new MessageManager();
        actionManager = new ActionManager();
        soundManager = new SoundManager();
        fillerManager = new FillerManager();
        vaultManager = new VaultManager();
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

        Bukkit.getScheduler().runTask(this, () -> {
            TranslatablePH.getInstance(this);
            if (engineHubManager.isWorldGuardInstalled())
                WorldGuardPH.getInstance(this);
            disguiseManager.load();
        });
    }

    @Override
    public void onDisable() {
        serializationLib.shutdown();
    }

    /**
     * Will reload all the managers
     */
    public void reload() {
        configManager.reload();
        listenerManager.reload();
        soundManager.reload();
        tagSetManager.reload();
        translatableManager.reload();
        translatableItemManager.reload();
        translatablePositionableManager.reload();
        translatablePositionableManager.reload();
        translatableAreaManager.reload();
        messageManager.reload();
        actionManager.reload();
        inventoryManager.reload();
        getPluginManager().reload();
    }

    public BlobLibAPI getAPI() {
        return api;
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

    public InventoryTrackerManager getInventoryTrackerManager() {
        return inventoryTrackerManager;
    }

    public TranslatableManager getTranslatableManager() {
        return translatableManager;
    }

    public LocalizableDataAssetManager<TranslatableItem> getTranslatableItemManager() {
        return translatableItemManager;
    }

    public LocalizableDataAssetManager<TranslatablePositionable> getTranslatablePositionableManager() {
        return translatablePositionableManager;
    }

    public LocalizableDataAssetManager<TranslatableArea> getTranslatableAreaManager() {
        return translatableAreaManager;
    }

    public DataAssetManager<TagSet> getTagSetManager() {
        return tagSetManager;
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

    /**
     * Will retrieve the DisguiseManager
     *
     * @return The DisguiseManager
     */
    public DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }
}
