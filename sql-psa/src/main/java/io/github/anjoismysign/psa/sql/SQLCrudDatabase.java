package io.github.anjoismysign.psa.sql;

import io.github.anjoismysign.psa.crud.CrudDatabase;
import org.jetbrains.annotations.NotNull;

public interface SQLCrudDatabase extends CrudDatabase {
    @NotNull
    SQLContainer generateContainer();
}
