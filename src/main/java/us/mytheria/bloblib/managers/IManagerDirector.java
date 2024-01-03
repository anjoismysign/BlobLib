package us.mytheria.bloblib.managers;

import us.mytheria.bloblib.entities.IFileManager;

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
}
