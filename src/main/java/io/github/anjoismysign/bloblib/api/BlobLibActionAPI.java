package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.action.Action;
import io.github.anjoismysign.bloblib.managers.ActionManager;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class BlobLibActionAPI {
    private static BlobLibActionAPI instance;
    private final BlobLib plugin;

    private BlobLibActionAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibActionAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibActionAPI.instance = new BlobLibActionAPI(plugin);
        }
        return instance;
    }

    public static BlobLibActionAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The action manager
     */
    public ActionManager getActionManager() {
        return plugin.getActionManager();
    }

    /**
     * @param key The key of the action
     * @return The action
     */
    @Nullable
    public Action<Entity> getAction(String key) {
        return getActionManager().getAction(key);
    }
}
