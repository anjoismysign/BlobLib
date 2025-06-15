package io.github.anjoismysign.psa.sql;

import java.io.File;
import java.sql.Connection;
import java.util.logging.Logger;

public final class SQLite extends SQLDatabase {
    SQLite(String name, File directory, Logger logger) {
        super(null, 0, name, null, null, logger);
        File file = new File(directory, this.database.endsWith(".db") ? this.database : this.database + ".db");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        this.dataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;

        try {
            connection = this.dataSource.getConnection();
        } catch (Exception var3) {
            var3.printStackTrace();
            this.getLogger().severe("Failed to connect to 'SQLite' using database: " + this.database);
        }

        return connection;
    }
}
