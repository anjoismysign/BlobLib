package us.mytheria.bloblib;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.hologram.HologramManager;
import us.mytheria.bloblib.managers.ChatListenerManager;
import us.mytheria.bloblib.managers.FileManager;
import us.mytheria.bloblib.managers.LangManager;
import us.mytheria.bloblib.vault.VaultManager;

public final class BlobLib extends JavaPlugin {
    private VaultManager vaultManager;
    private HologramManager hologramManager;
    private FileManager fileManager;
    private LangManager langManager;
    private ChatListenerManager listenerManager;

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.fileManager = new FileManager();
        this.langManager = new LangManager();
        vaultManager = new VaultManager();
        hologramManager = new HologramManager();
        listenerManager = new ChatListenerManager();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ChatListenerManager getListenerManager() {
        return listenerManager;
    }
}
