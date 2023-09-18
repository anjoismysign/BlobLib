package us.mytheria.bloblib.reflection;

import org.bukkit.Bukkit;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.reflection.nonlivingentity.NLEWrapper1_20_R1;
import us.mytheria.bloblib.reflection.nonlivingentity.NonLivingEntityWrapper;

public class BlobReflectionLib {
    private static BlobReflectionLib instance;
    private final NonLivingEntityWrapper nleWrapper;
    private final BlobLib plugin;

    private BlobReflectionLib(BlobLib plugin) {
        this.plugin = plugin;
        String nmsVersion = getNMSVersion();
        if (!nmsVersion.equals("1_20_R1"))
            BlobLib.getAnjoLogger().singleError("Unsupported NMS version: " + nmsVersion);
        this.nleWrapper = new NLEWrapper1_20_R1();
    }

    public static BlobReflectionLib getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobReflectionLib.instance = new BlobReflectionLib(plugin);
        }
        return instance;
    }

    public static BlobReflectionLib getInstance() {
        return getInstance(null);
    }

    public NonLivingEntityWrapper getNonLivingEntityWrapper() {
        return nleWrapper;
    }

    public String getNMSVersion() {
        String v = Bukkit.getServer().getClass().getPackage().getName();
        v = v.substring(v.lastIndexOf('.') + 1);
        if (v.startsWith("v_"))
            v = v.substring(2);
        if (v.startsWith("v"))
            v = v.substring(1);
        return v;
    }
}
