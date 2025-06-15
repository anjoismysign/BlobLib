package io.github.anjoismysign.psa.sql;

import io.github.anjoismysign.aesthetic.NamingConventions;
import io.github.anjoismysign.psa.crud.Crudable;
import io.github.anjoismysign.psa.crud.DatabaseCredentials;
import io.github.anjoismysign.psa.crud.MySQLCrudManager;
import io.github.anjoismysign.psa.crud.SQLCrudManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;

public record MySQLCrudDatabase(
        @NotNull String getHostName,
        int getPort,
        @NotNull String getDatabase,
        @NotNull String getUser,
        @NotNull String getPassword,
        @NotNull String getTableName,
        @NotNull String getPrimaryKeyName,
        int getPrimaryKeyLength,
        @NotNull String getCrudableKeyTypeName,
        @Nullable Logger getLogger
) implements SQLCrudDatabase {
    public static <T extends Crudable> MySQLCrudDatabase of(@NotNull Class<T> type, @NotNull DatabaseCredentials databaseCredentials) {
        if (databaseCredentials.isLocalhost()) {
            throw new IllegalStateException("'databaseCredentials' is meant to be used as localhost");
        } else {
            String name = type.getSimpleName();
            String tableName = "tbl" + NamingConventions.toPascalCase(name) + "s";
            String crudableKeyTypeName = name.toUpperCase(Locale.ROOT);
            int primaryKeyLength = 36;
            String primaryKeyName = databaseCredentials.getIdentifier().name().replace("-", "");
            return new MySQLCrudDatabase(
                    databaseCredentials.getHostname(),
                    databaseCredentials.getPort(),
                    databaseCredentials.getDatabase(),
                    databaseCredentials.getUsername(),
                    databaseCredentials.getPassword(),
                    tableName,
                    primaryKeyName,
                    primaryKeyLength,
                    crudableKeyTypeName,
                    databaseCredentials.getLogger()
            );
        }
    }

    @NotNull
    @Override
    public SQLContainer generateContainer() {
        final SQLDatabase database = new MySQL(this.getHostName, this.getPort, this.getDatabase, this.getUser, this.getPassword, this.getLogger);
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
        return new MySQLCrudManager<>(this, createFunction);
    }
}
