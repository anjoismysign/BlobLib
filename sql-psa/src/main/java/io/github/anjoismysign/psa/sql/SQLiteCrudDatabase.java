package io.github.anjoismysign.psa.sql;

import io.github.anjoismysign.aesthetic.NamingConventions;
import io.github.anjoismysign.psa.crud.Crudable;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.crud.SQLCrudManager;
import io.github.anjoismysign.psa.crud.SQLiteCrudManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;

public record SQLiteCrudDatabase(
        @NotNull String getName,
        @NotNull File getDirectory,
        @NotNull String getTableName,
        @NotNull String getPrimaryKeyName,
        int getPrimaryKeyLength,
        @NotNull String getCrudableKeyTypeName,
        @Nullable Logger getLogger
) implements SQLCrudDatabase {
    public static <T extends Crudable> SQLiteCrudDatabase of(@NotNull Class<T> type, @NotNull DatabaseCredentials databaseCredentials) {
        String name = type.getSimpleName();
        String tableName = "tbl" + NamingConventions.toPascalCase(name) + "s";
        String crudableTypeName = name.toUpperCase(Locale.ROOT);
        int primaryKeyLength = 36;
        String primaryKeyName = "UUID";
        return new SQLiteCrudDatabase(
                "database", databaseCredentials.getDirectory(), tableName, primaryKeyName, primaryKeyLength, crudableTypeName, databaseCredentials.getLogger()
        );
    }

    @NotNull
    @Override
    public SQLContainer generateContainer() {
        final SQLDatabase database = new SQLite(this.getName, this.getDirectory, this.getLogger);
        return new SQLContainer() {
            @Override
            public SQLDatabase getDatabase() {
                return database;
            }

            @Override
            public void disconnect() {
                database.disconnect();
            }
        };
    }

    @NotNull
    public <T extends Crudable> SQLCrudManager<T> crudManagerOf(@NotNull Function<String, T> createFunction) {
        return new SQLiteCrudManager<>(this, createFunction);
    }
}
