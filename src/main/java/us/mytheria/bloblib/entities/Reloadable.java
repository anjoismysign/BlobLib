package us.mytheria.bloblib.entities;

public interface Reloadable {

    /**
     * Returns whether the Reloadable is reloading.
     *
     * @return {@code true} if the Reloadable is reloading, {@code false} otherwise.
     */
    boolean isReloading();

    /**
     * Reloads the Reloadable.
     */
    void reload();
}
