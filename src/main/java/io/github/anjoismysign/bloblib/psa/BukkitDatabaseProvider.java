package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public enum BukkitDatabaseProvider {
    INSTANCE;

    @Nullable
    private PluginDatabaseProvider databaseProvider;

    private static final PluginDatabaseProvider SQL = plugin -> {
        Objects.requireNonNull(plugin, "'plugin' cannot be null");
        File directory = plugin.getDataFolder();
        return SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, directory);
    };

    /**
     * Gets the current DatabaseProvider
     *
     * @return the current DatabaseProvider, null in case it's not set yet
     */
    public @NotNull PluginDatabaseProvider getDatabaseProvider() {
        return SQL;
    }

    /**
     * Sets the DatabaseProvider meant for use
     *
     * @param databaseProvider the DatabaseProvider
     */
    public void setDatabaseProvider(@NotNull PluginDatabaseProvider databaseProvider) {
//        Objects.requireNonNull(databaseProvider, "'databaseProvider' cannot be null!");
//        if (this.databaseProvider != null)
//            return;
//        this.databaseProvider = databaseProvider;
    }

}
