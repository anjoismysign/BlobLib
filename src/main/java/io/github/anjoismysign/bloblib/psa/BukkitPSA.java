package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.aesthetic.DependencyLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public enum BukkitPSA {
    INSTANCE;

    public void load(@NotNull BlobLib blobLib) {
//        File directory = new File(blobLib.getDataFolder(), "psa");
//        if (!directory.exists())
//            directory.mkdirs();
//        DependencyLoader.INSTANCE.load(
//                BukkitDatabaseProvider.INSTANCE::setDatabaseProvider,
//                PluginDatabaseProvider.class,
//                directory);
    }
}
