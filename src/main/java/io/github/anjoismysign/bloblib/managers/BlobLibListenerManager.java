package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.listeners.DisplayUnriding;
import io.github.anjoismysign.bloblib.listeners.ProjectileDamage;
import io.github.anjoismysign.bloblib.listeners.TranslatableAreaWand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class BlobLibListenerManager {
    private static BlobLibListenerManager instance;

    public static BlobLibListenerManager getInstance(BlobLibConfigManager configManager) {
        if (instance == null) {
            if (configManager == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibListenerManager.instance = new BlobLibListenerManager(configManager);
        }
        return instance;
    }

    public static BlobLibListenerManager getInstance() {
        return getInstance(null);
    }

    private final DisplayUnriding displayUnriding;
    private final ProjectileDamage projectileDamage;
    private final TranslatableAreaWand areaWand;

    private BlobLibListenerManager(BlobLibConfigManager configManager) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        boolean alternativeSaving = pluginManager.isPluginEnabled("AlternativeSaving");
        boolean blobTycoon = pluginManager.isPluginEnabled("BlobTycoon");
        this.displayUnriding = new DisplayUnriding(configManager);
        this.projectileDamage = new ProjectileDamage();
        this.areaWand = new TranslatableAreaWand();
    }

    public void reload() {
        displayUnriding.reload();
    }

    public TranslatableAreaWand getAreaWand() {
        return areaWand;
    }
}