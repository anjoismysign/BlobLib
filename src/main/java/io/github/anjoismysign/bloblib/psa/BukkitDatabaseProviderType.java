package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.jetbrains.annotations.NotNull;

public enum BukkitDatabaseProviderType {
    SQLITE(() -> SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, BlobLib.getInstance().getDataFolder())),
    MYSQL(() -> {
        String connectionString = BukkitDatabaseProvider.INSTANCE.getSettings().connectionString();
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("database-settings.yml 'connectionString' cannot be null or empty");
        }
        return SQLDatabaseCredentials.ofConnectionString(connectionString);
    });

    private final PluginDatabaseProvider provider;

    BukkitDatabaseProviderType(@NotNull PluginDatabaseProvider provider) {
        this.provider = provider;
    }


    public PluginDatabaseProvider getDatabaseProvider() {
        return provider;
    }
}
