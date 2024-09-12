package us.mytheria.bloblib;

import org.bukkit.NamespacedKey;
import us.mytheria.bloblib.itemapi.DiscriminatorItemAPI;

public class SoulAPI implements DiscriminatorItemAPI {
    private static SoulAPI instance;
    private final NamespacedKey namespacedKey;

    private SoulAPI(BlobLib blobLib) {
        this.namespacedKey = new NamespacedKey(blobLib, "soul");
    }

    protected static SoulAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            SoulAPI.instance = new SoulAPI(blobLib);
        }
        return instance;
    }

    public static SoulAPI getInstance() {
        return getInstance(null);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}