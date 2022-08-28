package us.mytheria.bloblib;

import org.bukkit.plugin.java.JavaPlugin;

public final class BlobLib extends JavaPlugin {

    private static BlobLib instance;

    public static BlobLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }
}
