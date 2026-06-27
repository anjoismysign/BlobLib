package io.github.anjoismysign.bloblib.psa;

import org.jetbrains.annotations.NotNull;

public enum BukkitDatabaseProvider {
    INSTANCE;

    BukkitDatabaseProvider() {
        settings = DatabaseSettings.LOAD();
    }

    private final DatabaseSettings settings;

    /**
     * Gets the current DatabaseProvider
     *
     * @return the current DatabaseProvider, null in case it's not set yet
     */
    public @NotNull PluginDatabaseProvider getDatabaseProvider() {
        return settings.provider().getDatabaseProvider();
    }

    public DatabaseSettings getSettings() {
        return settings;
    }
}
