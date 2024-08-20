package us.mytheria.bloblib;

import org.bukkit.NamespacedKey;
import us.mytheria.bloblib.itemapi.ItemAPIType;
import us.mytheria.bloblib.itemapi.ValueItemAPI;

public class FluidPressureAPI implements ValueItemAPI<Double> {
    private static FluidPressureAPI instance;
    private final NamespacedKey namespacedKey;

    protected static FluidPressureAPI getInstance(BlobLib blobLib) {
        if (instance == null) {
            if (blobLib == null)
                throw new NullPointerException("injected dependency is null");
            FluidPressureAPI.instance = new FluidPressureAPI(blobLib);
        }
        return instance;
    }

    public static FluidPressureAPI getInstance() {
        return getInstance(null);
    }

    private FluidPressureAPI(BlobLib blobLib) {
        this.namespacedKey = new NamespacedKey(blobLib, "pressure");
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public ItemAPIType getAPIType() {
        return ItemAPIType.DOUBLE;
    }
}
