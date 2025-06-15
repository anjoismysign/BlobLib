package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.itemapi.DiscriminatorItemAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class UniqueAPI implements DiscriminatorItemAPI {
    private static UniqueAPI instance;
    private final NamespacedKey namespacedKey;

    private UniqueAPI(BlobLib blobLib) {
        this.namespacedKey = new NamespacedKey(blobLib, "unique");
    }

    protected static UniqueAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            UniqueAPI.instance = new UniqueAPI(blobLib);
        }
        return instance;
    }

    public static UniqueAPI getInstance() {
        return getInstance(null);
    }

    @Override
    public void set(PersistentDataHolder holder) {
        UUID random = UUID.randomUUID();
        holder.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, random.toString());
    }

    @Override
    public boolean isInstance(PersistentDataHolder holder) {
        PersistentDataContainer container = holder.getPersistentDataContainer();
        return container.has(namespacedKey, PersistentDataType.STRING);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}