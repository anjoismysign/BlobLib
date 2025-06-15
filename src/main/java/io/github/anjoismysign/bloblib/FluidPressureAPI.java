package io.github.anjoismysign.bloblib;

import io.github.anjoismysign.bloblib.itemapi.ItemAPIType;
import io.github.anjoismysign.bloblib.itemapi.ValueItemAPI;
import org.bukkit.NamespacedKey;

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
