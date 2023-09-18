package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.FileConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.TinyEventListener;

public class BlobLibConfigManager {
    // Singleton
    private static BlobLibConfigManager instance;

    public static BlobLibConfigManager getInstance(BlobLib director) {
        if (instance == null) {
            if (director == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibConfigManager.instance = new BlobLibConfigManager(director);
        }
        return instance;
    }

    public static BlobLibConfigManager getInstance() {
        return getInstance(null);
    }
    // Initium

    private final BlobLib plugin;
    private TinyEventListener displayRiding;
    private FileConfiguration configuration;

    private BlobLibConfigManager(BlobLib plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        configuration = plugin.getConfig();
        displayRiding = TinyEventListener.READ(configuration, "Display-Unriding");
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public TinyEventListener getDisplayRiding() {
        return displayRiding;
    }
}