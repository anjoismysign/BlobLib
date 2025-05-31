package me.anjoismysign.psa.sql;

import me.anjoismysign.psa.crud.CrudDatabase;
import org.jetbrains.annotations.NotNull;

public interface SQLCrudDatabase extends CrudDatabase {
   @NotNull
   SQLContainer generateContainer();
}
