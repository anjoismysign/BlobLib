package us.mytheria.bloblib.psa;

import me.anjoismysign.psa.crud.CrudDatabaseCredentials;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface PluginDatabaseProvider {

    @NotNull
    CrudDatabaseCredentials of(@NotNull Plugin plugin);

}
