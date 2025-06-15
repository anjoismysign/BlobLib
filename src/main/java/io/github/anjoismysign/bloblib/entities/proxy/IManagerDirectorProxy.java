package io.github.anjoismysign.bloblib.entities.proxy;

import io.github.anjoismysign.bloblib.entities.IFileManager;
import io.github.anjoismysign.bloblib.managers.IManagerDirector;

public class IManagerDirectorProxy implements IManagerDirector {
    private final IManagerDirector managerDirector;

    protected IManagerDirectorProxy(IManagerDirector managerDirector) {
        this.managerDirector = managerDirector;
    }

    public void unload() {
        managerDirector.unload();
    }

    public void postWorld() {
        managerDirector.postWorld();
    }

    public void reloadAll() {
        managerDirector.reloadAll();
    }

    public boolean isReloading() {
        return managerDirector.isReloading();
    }

    public IFileManager getFileManager() {
        return managerDirector.getFileManager();
    }
}
