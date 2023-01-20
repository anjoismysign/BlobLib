package us.mytheria.bloblib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.enginehub.EngineHubManager;
import us.mytheria.bloblib.hologram.HologramManager;
import us.mytheria.bloblib.managers.*;
import us.mytheria.bloblib.managers.fillermanager.FillerManager;
import us.mytheria.bloblib.vault.VaultManager;

public final class BlobLib extends JavaPlugin {
    private VaultManager vaultManager;
    private EngineHubManager engineHubManager;
    private HologramManager hologramManager;
    private FileManager fileManager;
    private InventoryManager inventoryManager;
    private MessageManager messageManager;
    private SoundManager soundManager;
    private FillerManager fillerManager;
    private ChatListenerManager chatManager;
    private SelPosListenerManager positionManager;
    private SelectorListenerManager selectorManager;
    private VariableSelectorManager variableSelectorManager;
    private DropListenerManager dropListenerManager;

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Jitpack test");
        instance = this;
        fileManager = new FileManager();
        inventoryManager = new InventoryManager();
        messageManager = new MessageManager();
        soundManager = new SoundManager();
        fillerManager = new FillerManager();
        vaultManager = new VaultManager();
        engineHubManager = new EngineHubManager();
        hologramManager = new HologramManager();
        chatManager = new ChatListenerManager();
        positionManager = new SelPosListenerManager();
        selectorManager = new SelectorListenerManager();
        variableSelectorManager = new VariableSelectorManager();
        dropListenerManager = new DropListenerManager();

        //Load reloadable managers
        reload();
    }

    public void reload() {
        soundManager.reload();
        messageManager.reload();
        inventoryManager.reload();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ChatListenerManager getChatManager() {
        return chatManager;
    }

    public SelPosListenerManager getPositionManager() {
        return positionManager;
    }

    public SelectorListenerManager getSelectorManager() {
        return selectorManager;
    }

    public VariableSelectorManager getVariableSelectorManager() {
        return variableSelectorManager;
    }

    public FillerManager getFillerManager() {
        return fillerManager;
    }

    public DropListenerManager getDropListenerManager() {
        return dropListenerManager;
    }

    public EngineHubManager getEngineHubManager() {
        return engineHubManager;
    }
}
