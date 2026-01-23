package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public enum BukkitDatabaseProvider {
    INSTANCE;

    private static final PluginDatabaseProvider SQL = new PluginDatabaseProvider() {
        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin) {
            return  of(plugin, plugin.getDataFolder());
        }

        @Override
        public @NotNull CrudDatabaseCredentials of(@NotNull Plugin plugin, @NotNull File directory) {
            Objects.requireNonNull(plugin, "'plugin' cannot be null");
            Objects.requireNonNull(directory, "'directory' cannot be null");
            if (!directory.isDirectory()){
                throw new RuntimeException(directory.getAbsolutePath() + " is not a directory");
            }
            return SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, directory);
        }
    };

    /**
     * Gets the current DatabaseProvider
     *
     * @return the current DatabaseProvider, null in case it's not set yet
     */
    public @NotNull PluginDatabaseProvider getDatabaseProvider() {
        return SQL;
    }

}
