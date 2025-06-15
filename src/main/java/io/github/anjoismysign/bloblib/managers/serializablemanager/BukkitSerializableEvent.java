package io.github.anjoismysign.bloblib.managers.serializablemanager;

import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitSerializableEvent<T extends LehmappSerializable> extends Event {
    protected final T serializable;

    public BukkitSerializableEvent(@NotNull T serializable,
                                   boolean isAsync) {
        super(isAsync);
        this.serializable = serializable;
    }
}
