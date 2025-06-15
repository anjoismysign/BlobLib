package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.managers.Manager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;

import java.util.HashSet;
import java.util.Set;

public abstract class ListenerManager extends Manager {
    private final Set<BlobListener> listeners;

    public ListenerManager(ManagerDirector managerDirector) {
        super(managerDirector);
        listeners = new HashSet<>();
    }

    public void reload() {
        listeners.forEach(BlobListener::reload);
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    public void add(BlobListener... listeners) {
        for (BlobListener listener : listeners) {
            this.listeners.add(listener);
        }
    }

    public void remove(BlobListener... listeners) {
        for (BlobListener listener : listeners) {
            this.listeners.remove(listener);
        }
    }
}
