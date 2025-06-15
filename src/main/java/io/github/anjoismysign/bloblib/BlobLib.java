package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.command.BlobLibCommand;
import io.github.anjoismysign.bloblib.disguises.DisguiseManager;
import io.github.anjoismysign.bloblib.enginehub.EngineHubManager;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.logger.BlobPluginLogger;
import io.github.anjoismysign.bloblib.entities.positionable.Positionable;
import io.github.anjoismysign.bloblib.entities.positionable.PositionableIO;
import io.github.anjoismysign.bloblib.entities.tag.TagSet;
import io.github.anjoismysign.bloblib.entities.tag.TagSetReader;
import io.github.anjoismysign.bloblib.entities.translatable.BlobTranslatablePositionable;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatablePositionable;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableReader;
import io.github.anjoismysign.bloblib.events.BlobLibReloadEvent;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.hologram.HologramManager;
import io.github.anjoismysign.bloblib.managers.ActionManager;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.managers.BlobLibFileManager;
import io.github.anjoismysign.bloblib.managers.BlobLibListenerManager;
import io.github.anjoismysign.bloblib.managers.ChatListenerManager;
import io.github.anjoismysign.bloblib.managers.ColorManager;
import io.github.anjoismysign.bloblib.managers.DataAssetManager;
import io.github.anjoismysign.bloblib.managers.DropListenerManager;
import io.github.anjoismysign.bloblib.managers.InventoryManager;
import io.github.anjoismysign.bloblib.managers.InventoryTrackerManager;
import io.github.anjoismysign.bloblib.managers.LocalizableDataAssetManager;
import io.github.anjoismysign.bloblib.managers.MessageManager;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.managers.ScriptManager;
import io.github.anjoismysign.bloblib.managers.SelPosListenerManager;
import io.github.anjoismysign.bloblib.managers.SelectorListenerManager;
import io.github.anjoismysign.bloblib.managers.SoundManager;
import io.github.anjoismysign.bloblib.managers.TranslatableAreaManager;
import io.github.anjoismysign.bloblib.managers.TranslatableManager;
import io.github.anjoismysign.bloblib.managers.VariableSelectorManager;
import io.github.anjoismysign.bloblib.managers.fillermanager.FillerManager;
import io.github.anjoismysign.bloblib.placeholderapi.TranslatablePH;
import io.github.anjoismysign.bloblib.placeholderapi.WorldGuardPH;
import io.github.anjoismysign.bloblib.psa.BukkitPSA;
import io.github.anjoismysign.bloblib.utilities.MinecraftVersion;
import io.github.anjoismysign.bloblib.utilities.SerializationLib;
import io.github.anjoismysign.bloblib.vault.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the plugin
 */
public class BlobLib extends JavaPlugin {
    private static BlobPluginLogger anjoLogger;

    private SerializationLib serializationLib;

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
    private InventoryTrackerManager inventoryTrackerManager;
    private TranslatableManager translatableManager;

    private LocalizableDataAssetManager<TranslatableItem> translatableItemManager;
    private LocalizableDataAssetManager<TranslatablePositionable> translatablePositionableManager;
    private TranslatableAreaManager translatableAreaManager;
    private DataAssetManager<TagSet> tagSetManager;

    private MinecraftVersion running;
    private SoulAPI soulAPI;
    private UniqueAPI uniqueAPI;
    private FluidPressureAPI fluidPressureAPI;
    private ProjectileDamageAPI projectileDamageAPI;

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
        serializationLib = SerializationLib.getInstance();
        BukkitPSA.INSTANCE.load(this);
        running = MinecraftVersion.getRunning();
        soulAPI = SoulAPI.getInstance(this);
        uniqueAPI = UniqueAPI.getInstance(this);
        fluidPressureAPI = FluidPressureAPI.getInstance(this);
        projectileDamageAPI = ProjectileDamageAPI.getInstance(this);
        api = BlobLibAPI.getInstance(this);
        bloblibupdater = new BlobLibUpdater(this);
        anjoLogger = new BlobPluginLogger(this);
        scriptManager = new ScriptManager();
        pluginManager = PluginManager.getInstance();
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
                            Positionable positionable = PositionableIO.INSTANCE.read(section);
                            if (!section.isString("Display"))
                                throw new ConfigurationFieldException("'Display' is missing or not set");
                            String display = section.getString("Display");
                            return BlobTranslatablePositionable.of(key, locale, display, positionable);
                        },
                        DataAssetType.TRANSLATABLE_POSITIONABLE,
                        section -> section.isDouble("X") && section.isDouble("Y") && section.isDouble("Z"));
        translatableAreaManager = TranslatableAreaManager.of();
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
        BlobLibCommand.INSTANCE.initialize();

        Bukkit.getScheduler().runTask(this, () -> {
            TranslatablePH.getInstance(this);
            if (engineHubManager.isWorldGuardInstalled())
                WorldGuardPH.getInstance(this);
            disguiseManager.load();
        });
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

        BlobLibReloadEvent reloadEvent = new BlobLibReloadEvent();
        Bukkit.getPluginManager().callEvent(reloadEvent);
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

    public TranslatableAreaManager getTranslatableAreaManager() {
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
