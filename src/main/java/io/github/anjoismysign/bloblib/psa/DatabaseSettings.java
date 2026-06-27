package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

public record DatabaseSettings(@NotNull String providerName,
                               @Nullable String connectionString) {

    public static DatabaseSettings LOAD(){
        BlobLib blobLib = new BlobLib();
        blobLib.saveResource("database-settings.yml", false);
        File file = blobLib.getDataFolder();
        if (!file.exists()){
            file.mkdirs();
        }
        File databaseSettings = new File(file.getPath() + File.separator + "database-settings.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(databaseSettings);
        String providerName = config.getString("provider-name", "SQLITE");
        String connectionString = config.getString("connection-string");
        return new DatabaseSettings(providerName, connectionString);
    }

    @NotNull
    public BukkitDatabaseProviderType provider(){
        try {
            return BukkitDatabaseProviderType.valueOf(providerName);
        }  catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid database-settings.yml provider name: " + providerName + "\n Valid values are: " + Arrays.toString(BukkitDatabaseProviderType.values()));
        }
    }

}
