package us.mytheria.bloblib;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.entities.chatlistener.ChatListenerManager;
import us.mytheria.bloblib.hologram.HologramManager;
import us.mytheria.bloblib.vault.VaultManager;

public final class BlobLib extends JavaPlugin {
    private VaultManager vaultManager;
    private HologramManager hologramManager;
    private ChatListenerManager listenerManager;

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        vaultManager = new VaultManager();
        hologramManager = new HologramManager();
        listenerManager = new ChatListenerManager();
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
