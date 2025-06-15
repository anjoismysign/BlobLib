package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface PluginDatabaseProvider {

    @NotNull
    CrudDatabaseCredentials of(@NotNull Plugin plugin);

}
