package io.github.anjoismysign.bloblib.entities;

public interface RunnableReloadable extends Reloadable {

    /**
     * Adds a Runnable to be run when the RunnableReloadable is reloaded.
     *
     * @param runnable The Runnable to run when the RunnableReloadable is reloaded.
     */
    void whenReloaded(Runnable runnable);
}
