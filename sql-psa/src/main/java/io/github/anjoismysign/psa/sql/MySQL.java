package io.github.anjoismysign.psa.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class MySQL extends SQLDatabase {
    MySQL(String hostName, int port, String database, String user, String password, Logger logger) {
        super(hostName, port, database, user, password, logger);
        this.dataSource.setJdbcUrl("jdbc:mysql://" + this.hostName + ":" + this.port + "/" + this.database);
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;

        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException var3) {
            var3.printStackTrace();
            this.getLogger()
                    .severe(
                            "Failed to connect to 'MySQL' Database using following credentials: \nHostname: '"
                                    + this.hostName
                                    + "', Port: '"
                                    + this.port
                                    + "', Database: '"
                                    + this.database
                                    + "', User: '"
                                    + this.user
                                    + "'. \nBe sure that password match!\n\n"
                    );
        }

        return connection;
    }
}
