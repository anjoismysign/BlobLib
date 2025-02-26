package us.mytheria.bloblib.psa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum BukkitDatabaseProvider {
    INSTANCE;

    @Nullable
    private PluginDatabaseProvider databaseProvider;

    /**
     * Gets the current DatabaseProvider
     *
     * @return the current DatabaseProvider, null in case it's not set yet
     */
    @Nullable
    public PluginDatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    /**
     * Sets the DatabaseProvider meant for use
     *
     * @param databaseProvider the DatabaseProvider
     */
    public void setDatabaseProvider(@NotNull PluginDatabaseProvider databaseProvider) {
        Objects.requireNonNull(databaseProvider, "'databaseProvider' cannot be null!");
        if (this.databaseProvider != null)
            return;
        this.databaseProvider = databaseProvider;
    }

}
