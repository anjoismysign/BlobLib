package us.mytheria.bloblib.managers;

import us.mytheria.bloblib.entities.Reloadable;

public abstract class Manager implements Reloadable {
    private final ManagerDirector managerDirector;

    public Manager(ManagerDirector managerDirector) {
        this.managerDirector = managerDirector;
    }

    public void loadManually() {
    }

    public void reload() {
    }

    public void unload() {
    }

    public void postWorld() {
    }

    /**
     * Can be overridden to return whether the manager is reloading.
     *
     * @return {@code true} if the manager is reloading, {@code false} otherwise.
     */
    public boolean isReloading() {
        return false;
    }

    public ManagerDirector getManagerDirector() {
        return managerDirector;
    }

    public BlobPlugin getPlugin() {
        return managerDirector.getPlugin();
    }
}
