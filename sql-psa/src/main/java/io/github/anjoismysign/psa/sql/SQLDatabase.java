package io.github.anjoismysign.psa.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class SQLDatabase {
    protected final Logger logger;
    protected final String database;
    protected final String hostName;
    protected final int port;
    protected final String user;
    protected final String password;
    protected final HikariDataSource dataSource;
    protected final ThreadPoolExecutor executor;

    protected SQLDatabase(String hostName, int port, String database, String user, String password, Logger logger) {
        this.logger = logger;
        this.dataSource = new HikariDataSource();
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.database = database;
        this.hostName = hostName;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);
        this.dataSource.setMaximumPoolSize(200);
        this.dataSource.setMinimumIdle(5);
        this.dataSource.setLeakDetectionThreshold(15000L);
        this.dataSource.setConnectionTimeout(1000L);
    }

    public abstract Connection getConnection();

    public final void disconnect() {
        try {
            this.executor.shutdown();
            this.dataSource.close();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public final String getDatabase() {
        return this.database;
    }

    public ResultSet selectRowByPrimaryKey(String keyType, String key, String table) {
        try {
            Connection connection = this.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + keyType + "=?");
            preparedStatement.setString(1, key);
            return preparedStatement.executeQuery();
        } catch (SQLException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public PreparedStatement updateDataSet(String keyType, String table, String values) {
        try {
            Connection connection = this.getConnection();
            return connection.prepareStatement("UPDATE " + table + " SET " + values + " WHERE " + keyType + "=?");
        } catch (SQLException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public PreparedStatement delete(String table, String keyType) {
        try {
            Connection connection = this.getConnection();
            return connection.prepareStatement("DELETE FROM " + table + " WHERE " + keyType + "=?");
        } catch (SQLException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public boolean createTable(String table, String columns, String primaryKey) {
        Connection connection = null;

        boolean exception;
        try {
            connection = this.getConnection();
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ",PRIMARY KEY(" + primaryKey + "))"
                );
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            }

            exception = false;
        } catch (SQLException var17) {
            var17.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException var16) {
                var16.printStackTrace();
            }
        }

        return exception;
    }

    public boolean exists(String table, String keyType, String key) {
        Connection connection = null;

        try {
            connection = this.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + keyType + "='" + key + "'");
            return preparedStatement.executeQuery().next();
        } catch (SQLException var16) {
            var16.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException var15) {
                    var15.printStackTrace();
                }
            }
        }

        return false;
    }

    public void selectAllFromDatabase(String table, Consumer<ResultSet> forEach) {
        try {
            PreparedStatement statement = this.getConnection().prepareStatement("select * from " + table);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                forEach.accept(resultSet);
            }

            resultSet.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException var5) {
            var5.printStackTrace();
        }
    }

    public Logger getLogger() {
        return this.logger;
    }
}
