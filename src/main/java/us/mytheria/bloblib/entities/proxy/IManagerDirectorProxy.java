package us.mytheria.bloblib.entities.proxy;

import us.mytheria.bloblib.entities.IFileManager;
import us.mytheria.bloblib.managers.IManagerDirector;

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

    public IFileManager getFileManager() {
        return managerDirector.getFileManager();
    }
}
