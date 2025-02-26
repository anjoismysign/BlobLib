package us.mytheria.bloblib;

import org.bukkit.NamespacedKey;
import us.mytheria.bloblib.itemapi.ItemAPIType;
import us.mytheria.bloblib.itemapi.ValueItemAPI;

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
