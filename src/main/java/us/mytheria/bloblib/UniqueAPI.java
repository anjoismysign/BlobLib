package us.mytheria.bloblib;

import org.bukkit.NamespacedKey;
import us.mytheria.bloblib.itemapi.DiscriminatorItemAPI;

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

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
