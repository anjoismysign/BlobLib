package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.bloblib.content.ContentWarningListener;
import io.github.anjoismysign.bloblib.listener.DisplayUnriding;
import io.github.anjoismysign.bloblib.listener.ItemConsumeUpdateInventory;
import io.github.anjoismysign.bloblib.listener.ProjectileDamage;
import io.github.anjoismysign.bloblib.listener.TranslatableAreaWand;

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
    private final ItemConsumeUpdateInventory itemConsumeUpdateInventory;
    private final ContentWarningListener contentWarning;
    private final TranslatableAreaWand areaWand;

    private BlobLibListenerManager(BlobLibConfigManager configManager) {
        this.displayUnriding = new DisplayUnriding(configManager);
        this.itemConsumeUpdateInventory = new ItemConsumeUpdateInventory(configManager);
        this.contentWarning = new ContentWarningListener();
        this.areaWand = new TranslatableAreaWand();

        new ProjectileDamage();
    }

    public void reload() {
        displayUnriding.reload();
        itemConsumeUpdateInventory.reload();
        contentWarning.reload();
    }

    public TranslatableAreaWand getAreaWand() {
        return areaWand;
    }
}