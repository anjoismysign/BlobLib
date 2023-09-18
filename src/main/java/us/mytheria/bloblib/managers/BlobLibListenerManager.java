package us.mytheria.bloblib.managers;

import us.mytheria.bloblib.listeners.DisplayUnriding;

public class BlobLibListenerManager {
    // Singleton
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
    // Initium

    private final DisplayUnriding displayUnriding;

    private BlobLibListenerManager(BlobLibConfigManager configManager) {
        this.displayUnriding = new DisplayUnriding(configManager);
    }

    public void reload() {
        displayUnriding.reload();
    }
}