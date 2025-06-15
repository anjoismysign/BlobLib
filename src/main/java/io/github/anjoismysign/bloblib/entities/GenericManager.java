package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.Manager;

public abstract class GenericManager<T extends BlobPlugin, D extends GenericManagerDirector<T>> extends Manager {
    /**
     * managerDirector reference is stored for the reason
     * of not wanting to cast whenever using getter (should improve a little bit
     * of performance while sacrificing a little bit of memory).
     */
    private final D managerDirector;

    public GenericManager(D managerDirector) {
        super(managerDirector);
        this.managerDirector = managerDirector;
    }

    @Override
    public D getManagerDirector() {
        return managerDirector;
    }

    @Override
    public T getPlugin() {
        return managerDirector.getPlugin();
    }
}
