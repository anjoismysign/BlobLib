package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.action.Action;
import io.github.anjoismysign.bloblib.command.BlobLibCommand;
import io.github.anjoismysign.bloblib.disguises.DisguiseManager;
import io.github.anjoismysign.bloblib.entities.BlobMessageIO;
import io.github.anjoismysign.bloblib.entities.BlobSoundReader;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.logger.BlobPluginLogger;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import io.github.anjoismysign.bloblib.entities.positionable.Positionable;
import io.github.anjoismysign.bloblib.entities.positionable.PositionableIO;
import io.github.anjoismysign.bloblib.entities.tag.TagSet;
import io.github.anjoismysign.bloblib.entities.tag.TagSetIO;
import io.github.anjoismysign.bloblib.entities.translatable.BlobTranslatablePositionable;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatablePositionable;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableReader;
import io.github.anjoismysign.bloblib.events.BlobLibPreReloadEvent;
import io.github.anjoismysign.bloblib.events.BlobLibReloadEvent;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.hologram.HologramManager;
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
import io.github.anjoismysign.bloblib.managers.LootTableManager;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.managers.SelPosListenerManager;
import io.github.anjoismysign.bloblib.managers.SelectorListenerManager;
import io.github.anjoismysign.bloblib.managers.TranslatableAreaManager;
import io.github.anjoismysign.bloblib.managers.TranslatableManager;
import io.github.anjoismysign.bloblib.managers.VariableSelectorManager;
import io.github.anjoismysign.bloblib.managers.fillermanager.FillerManager;
import io.github.anjoismysign.bloblib.middleman.enginehub.EngineHubManager;
import io.github.anjoismysign.bloblib.middleman.skript.BlobLibSkriptAddon;
import io.github.anjoismysign.bloblib.placeholderapi.TranslatablePH;
import io.github.anjoismysign.bloblib.placeholderapi.WorldGuardPH;
import io.github.anjoismysign.bloblib.psa.BukkitPSA;
import io.github.anjoismysign.bloblib.utilities.MinecraftVersion;
import io.github.anjoismysign.bloblib.utilities.SerializationLib;
import io.github.anjoismysign.bloblib.vault.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the plugin
 */
public class BlobLib extends JavaPlugin {
    private static BlobPluginLogger anjoLogger;
    private static BlobLib instance;
    private SerializationLib serializationLib;
    private BlobLibUpdater bloblibupdater;
    private BlobLibAPI api;
    private VaultManager vaultManager;
    private EngineHubManager engineHubManager;
    private DisguiseManager disguiseManager;
    private HologramManager hologramManager;
    private BlobLibFileManager fileManager;
    private InventoryManager inventoryManager;
    private LocalizableDataAssetManager<BlobMessage> messageManager;
    private DataAssetManager<BlobSound> soundManager;
    private FillerManager fillerManager;
    private ChatListenerManager chatManager;
    private SelPosListenerManager positionManager;
    private SelectorListenerManager selectorManager;
    private VariableSelectorManager variableSelectorManager;
    private DropListenerManager dropListenerManager;
    private ColorManager colorManager;
    private PluginManager pluginManager;
    private DataAssetManager<Action<Entity>> actionManager;
    private BlobLibConfigManager configManager;
    private BlobLibListenerManager listenerManager;
    private InventoryTrackerManager inventoryTrackerManager;
    private TranslatableManager translatableManager;
    private LocalizableDataAssetManager<TranslatableItem> translatableItemManager;
    private LocalizableDataAssetManager<TranslatablePositionable> translatablePositionableManager;
    private TranslatableAreaManager translatableAreaManager;
    private DataAssetManager<TagSet> tagSetManager;
    private LootTableManager lootTableManager;
    private MinecraftVersion running;
    private SoulAPI soulAPI;
    private UniqueAPI uniqueAPI;
    private FluidPressureAPI fluidPressureAPI;
    private ProjectileDamageAPI projectileDamageAPI;

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
     * Will retrieve the VaultManager
     *
     * @return The VaultManager
     */
    public static VaultManager vaultManager() {
        return getInstance().getVaultManager();
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
        pluginManager = PluginManager.getInstance();
        colorManager = new ColorManager();
        fileManager = new BlobLibFileManager();
        fileManager.unpackYamlFile("/BlobInventory", "CurrencyBuilder", false);
        engineHubManager = EngineHubManager.getInstance();
        configManager = BlobLibConfigManager.getInstance(this);

        inventoryManager = new InventoryManager();
        inventoryTrackerManager = new InventoryTrackerManager();
        translatableManager = new TranslatableManager();
        tagSetManager = DataAssetManager.of(fileManager.getDirectory(DataAssetType.TAG_SET),
                TagSetIO::READ,
                DataAssetType.TAG_SET,
                section -> section.isList("Inclusions") ||
                        !section.getStringList("Include-Set").isEmpty(),
                TagSetIO::WRITE);
        lootTableManager = new LootTableManager(this);
        translatableItemManager = LocalizableDataAssetManager
                .of(fileManager.getDirectory(DataAssetType.TRANSLATABLE_ITEM),
                        TranslatableReader::ITEM,
                        DataAssetType.TRANSLATABLE_ITEM,
                        section -> section.isConfigurationSection("ItemStack"),
                        null);
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
                        section -> section.isDouble("X") && section.isDouble("Y") && section.isDouble("Z"),
                        PositionableIO.INSTANCE::write);
        translatableAreaManager = TranslatableAreaManager.of();
        messageManager = LocalizableDataAssetManager.of(fileManager.getDirectory(DataAssetType.BLOB_MESSAGE),
                (section, locale, key) -> BlobMessageIO.read(section, locale, key),
        DataAssetType.BLOB_MESSAGE,
        section -> section.isString("Type"),
        null);
        actionManager = DataAssetManager.of(fileManager.getDirectory(DataAssetType.ACTION),
                (section, key) -> Action.fromConfigurationSection(section),
                DataAssetType.ACTION,
                section -> section.contains("Type") && section.isString("Type"),
                null);
        soundManager = DataAssetManager.of(fileManager.getDirectory(DataAssetType.BLOB_SOUND),
                BlobSoundReader::read,
                DataAssetType.BLOB_SOUND,
                section -> section.contains("Sound"),
                null);
        fillerManager = new FillerManager();
        vaultManager = new VaultManager();
        disguiseManager = new DisguiseManager();
        hologramManager = new HologramManager();
        chatManager = new ChatListenerManager();
        positionManager = new SelPosListenerManager();
        selectorManager = new SelectorListenerManager();
        variableSelectorManager = new VariableSelectorManager();
        dropListenerManager = new DropListenerManager();
        listenerManager = BlobLibListenerManager.getInstance(configManager);

        //Load reloadable managers
        reload();
        BlobLibCommand.INSTANCE.initialize();

        Bukkit.getScheduler().runTask(this, () -> {
            TranslatablePH.getInstance(this);
            if (engineHubManager.isWorldGuardInstalled())
                WorldGuardPH.getInstance(this);
            disguiseManager.load();
            if (Bukkit.getPluginManager().isPluginEnabled("Skript")){
                new BlobLibSkriptAddon();
            }
        });
    }

    /**
     * Will reload all the managers
     */
    public void reload() {
        org.bukkit.plugin.PluginManager pluginManager = Bukkit.getPluginManager();
        BlobLibPreReloadEvent preReloadEvent = new BlobLibPreReloadEvent();
        pluginManager.callEvent(preReloadEvent);

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
        lootTableManager.reload();
        inventoryManager.reload();
        getPluginManager().reload();

        BlobLibReloadEvent reloadEvent = new BlobLibReloadEvent();
        pluginManager.callEvent(reloadEvent);
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

    public LootTableManager getLootTableManager() {
        return lootTableManager;
    }

    /**
     * Will retrieve the MessageManager
     *
     * @return The MessageManager
     */
    public LocalizableDataAssetManager<BlobMessage> getMessageManager() {
        return messageManager;
    }

    /**
     * Will retrieve the ActionManager
     *
     * @return The ActionManager
     */
    public DataAssetManager<Action<Entity>> getActionManager() {
        return actionManager;
    }

    /**
     * Will retrieve the SoundManager
     *
     * @return The SoundManager
     */
    public DataAssetManager<BlobSound> getSoundManager() {
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
