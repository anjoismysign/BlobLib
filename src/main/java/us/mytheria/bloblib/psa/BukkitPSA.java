package us.mytheria.bloblib.psa;

import me.anjoismysign.aesthetic.DependencyLoader;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;

import java.io.File;

public enum BukkitPSA {
    INSTANCE;

    public void load(@NotNull BlobLib blobLib) {
        File directory = new File(blobLib.getDataFolder(), "psa");
        if (!directory.exists())
            directory.mkdirs();
        DependencyLoader.INSTANCE.load(
                BukkitDatabaseProvider.INSTANCE::setDatabaseProvider,
                PluginDatabaseProvider.class,
                directory);
    }
}
