package us.mytheria.bloblib;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.vault.VaultManager;

public final class BlobLib extends JavaPlugin {

    private VaultManager vaultManager;

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        vaultManager = new VaultManager();
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }
}
