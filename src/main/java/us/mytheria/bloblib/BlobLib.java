package us.mytheria.bloblib;

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
    private MessageManager messageManager;
    private SoundManager soundManager;
    private FillerManager fillerManager;
    private ChatListenerManager chatManager;
    private SelPosListenerManager positionManager;
    private SelectorListenerManager selectorManager;
    private InventoryManager inventoryManager;
    private DropListenerManager dropListenerManager;

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        fileManager = new FileManager();
        messageManager = new MessageManager();
        soundManager = new SoundManager();
        fillerManager = new FillerManager();
        vaultManager = new VaultManager();
        engineHubManager = new EngineHubManager();
        hologramManager = new HologramManager();
        chatManager = new ChatListenerManager();
        positionManager = new SelPosListenerManager();
        selectorManager = new SelectorListenerManager();
        inventoryManager = new InventoryManager();
        dropListenerManager = new DropListenerManager();
    }

    public FileManager getFileManager() {
        return fileManager;
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

    public InventoryManager getInventoryManager() {
        return inventoryManager;
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
