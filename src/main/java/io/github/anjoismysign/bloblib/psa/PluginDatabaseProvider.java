package io.github.anjoismysign.bloblib.psa;

import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import org.jetbrains.annotations.NotNull;

public interface PluginDatabaseProvider {

    @NotNull
    CrudDatabaseCredentials of();

}
