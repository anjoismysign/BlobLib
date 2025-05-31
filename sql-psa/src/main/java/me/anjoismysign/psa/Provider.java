package me.anjoismysign.psa;

import java.io.File;
import java.util.Objects;
import me.anjoismysign.psa.crud.CrudDatabaseCredentials;
import me.anjoismysign.psa.crud.DatabaseCredentials;
import me.anjoismysign.psa.sql.SQLDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.psa.PluginDatabaseProvider;

public class Provider implements PluginDatabaseProvider {

    @NotNull
    public CrudDatabaseCredentials of(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "'plugin' cannot be null");
        File directory = plugin.getDataFolder();
        return SQLDatabaseCredentials.at(DatabaseCredentials.Identifier.UUID, directory);
    }

}
