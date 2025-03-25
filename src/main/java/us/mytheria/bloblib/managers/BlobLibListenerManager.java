package us.mytheria.bloblib.managers;

import us.mytheria.bloblib.listeners.DisplayUnriding;
import us.mytheria.bloblib.listeners.ProjectileDamage;
import us.mytheria.bloblib.listeners.TranslatableAreaWand;

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