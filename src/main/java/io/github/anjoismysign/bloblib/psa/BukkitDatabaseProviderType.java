package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
public enum BukkitDatabaseProviderType {
    SQLITE(new PluginDatabaseProvider() {
        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin) {
            return of(plugin, plugin.getDataFolder());
        }

        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin, @NotNull File directory) {
            Objects.requireNonNull(plugin, "'plugin' cannot be null");
            return SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, BlobLib.getInstance().getDataFolder());
        }
    }),
    MYSQL(new PluginDatabaseProvider() {
        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin) {
            String connectionString = BukkitDatabaseProvider.INSTANCE.getSettings().connectionString();
            if (connectionString == null || connectionString.isEmpty()) {
                throw new IllegalArgumentException("database-settings.yml 'connectionString' cannot be null or empty");
            }
            return SQLDatabaseCredentials.ofConnectionString(connectionString);
        }
        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin, @NotNull File directory) {
            return of(plugin);
        }
    });

    private final PluginDatabaseProvider provider;

    BukkitDatabaseProviderType(@NotNull PluginDatabaseProvider provider) {
        this.provider = provider;
    }


    public PluginDatabaseProvider getDatabaseProvider() {
        return provider;
    }
}
