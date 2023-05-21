package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.ManagerDirector;

public abstract class GenericManagerDirector<T extends BlobPlugin> extends ManagerDirector {
    /**
     * plugin reference is stored for the reason
     * of not wanting to cast whenever using getter (should improve a little bit
     * of performance while sacrificing a little bit of memory).
     */
    private final T plugin;

    public GenericManagerDirector(T plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public T getPlugin() {
        return plugin;
    }
}