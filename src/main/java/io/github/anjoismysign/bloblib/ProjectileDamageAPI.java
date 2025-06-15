package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.itemapi.ItemAPIType;
import io.github.anjoismysign.bloblib.itemapi.ValueItemAPI;
import org.bukkit.NamespacedKey;

public class ProjectileDamageAPI implements ValueItemAPI<Double> {
    private static ProjectileDamageAPI instance;
    private final NamespacedKey namespacedKey;

    protected static ProjectileDamageAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            ProjectileDamageAPI.instance = new ProjectileDamageAPI(blobLib);
        }
        return instance;
    }

    public static ProjectileDamageAPI getInstance() {
        return getInstance(null);
    }

    private ProjectileDamageAPI(BlobLib blobLib) {
        this.namespacedKey = new NamespacedKey(blobLib, "pressure");
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public ItemAPIType getAPIType() {
        return ItemAPIType.DOUBLE;
    }
}
