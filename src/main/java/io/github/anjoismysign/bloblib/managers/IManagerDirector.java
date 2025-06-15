package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.entities.IFileManager;

public interface IManagerDirector {
    /**
     * Will do unload/disable logic (synchronously).
     */
    void unload();

    /**
     * Will do logic once the main world(s) are loaded (synchronously)
     */
    void postWorld();

    /**
     * Will do reload logic (asynchronously).
     */
    void reloadAll();

    /**
     * Will get the file manager that's required by BlobLib.
     * If proxied, operations done on the file manager will
     * be done when loading the manager director, which
     * will not affect gameplay since this should be done
     * on server start or on test servers (if reloading).
     *
     * @return the file manager.
     */
    IFileManager getFileManager();

    /**
     * Whether the manager director is reloading.
     *
     * @return {@code true} if reloading, {@code false} otherwise.
     */
    boolean isReloading();
}
