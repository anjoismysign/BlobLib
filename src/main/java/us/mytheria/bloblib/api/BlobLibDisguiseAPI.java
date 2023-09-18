package us.mytheria.bloblib.api;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.disguises.Disguiser;

public class BlobLibDisguiseAPI {
    private static BlobLibDisguiseAPI instance;
    private final BlobLib plugin;

    private BlobLibDisguiseAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibDisguiseAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibDisguiseAPI.instance = new BlobLibDisguiseAPI(plugin);
        }
        return instance;
    }

    public static BlobLibDisguiseAPI getInstance() {
        return getInstance(null);
    }

    /**
     * Will return the Disguiser implementation
     * WARNING! This method will throw an IllegalStateException if called before the world loads!
     * <p>
     * Disguiser interface was made in order to support/cover
     * a wide range of disguise plugins while at the same time
     * allowing disguising methods to be called without failing
     * fast!
     * <p>
     * For future reference, if you want to use the Disguiser,
     * be sure to not cache it in a variable, as we might see
     * Plugman being updated :D
     *
     * @return the implementation
     */
    public Disguiser getDisguiser() {
        return plugin.getDisguiseManager().getDisguiser();
    }
}
