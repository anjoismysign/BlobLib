package io.github.anjoismysign.psa;

import io.github.anjoismysign.bloblib.psa.PluginDatabaseProvider;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Provider implements PluginDatabaseProvider {

    @NotNull
    public CrudDatabaseCredentials of(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "'plugin' cannot be null");
        File directory = plugin.getDataFolder();
        return SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, directory);
    }

}
