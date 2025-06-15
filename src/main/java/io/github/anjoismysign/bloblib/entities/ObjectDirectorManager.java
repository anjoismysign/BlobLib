package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.managers.Manager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;

import java.util.AbstractMap;
import java.util.function.Supplier;

public class ObjectDirectorManager extends Manager {
    private final Supplier<AbstractMap<Class<? extends BlobObject>, ObjectDirector<? extends BlobObject>>> mapSupplier;
    private AbstractMap<Class<? extends BlobObject>, ObjectDirector<? extends BlobObject>> directors;

    public ObjectDirectorManager(ManagerDirector managerDirector,
                                 Supplier<AbstractMap<Class<? extends BlobObject>, ObjectDirector<? extends BlobObject>>> mapSupplier) {
        super(managerDirector);
        this.mapSupplier = mapSupplier;
        reload();
    }

    @Override
    public void reload() {
        initializeObjects();
    }

    private void initializeObjects() {
        directors = mapSupplier.get();
    }

    public <T extends BlobObject> void addObjectDirector(Class<T> clazz, ObjectDirector<T> objectDirector) {
        directors.put(clazz, objectDirector);
    }

    @SuppressWarnings("unchecked")
    public <T extends BlobObject> ObjectDirector<T> getObjectDirector(Class<T> clazz) {
        return (ObjectDirector<T>) directors.get(clazz);
    }
}
