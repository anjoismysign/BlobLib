package us.mytheria.bloblib.jlib.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.StorageAction;
import us.mytheria.bloblib.jlib.storage.database.wrapped.WrappedParameters;
import us.mytheria.bloblib.jlib.storage.database.wrapped.WrappedResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public abstract class SQLDatabase extends Database {
    protected final HikariDataSource dataSource = new HikariDataSource();
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * Constructs a new SQLDatabase instance, shouldn't be used externally
     * @param plugin The JavaPlugin associated with the MySQL Database
     * @param hostName The host name of the MySQL Server
     * @param port The port of the MySQL Server
     * @param database The name of the MySQL Database
     * @param user The user to use
     * @param password The password to use
     */
    protected SQLDatabase(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(plugin, hostName, port, database, user, password);
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);
        this.dataSource.setMaximumPoolSize(200);
        this.dataSource.setMinimumIdle(5);
        this.dataSource.setLeakDetectionThreshold(15000);
        this.dataSource.setConnectionTimeout(1000);
    }

    /**
     * Returns the Connection for the SQLDatabase
     * @return The Connection
     * @see Connection
     */
    public abstract Connection getConnection();

    /**
     * Connects to the SQLDatabase
     */
    @Override
    public final void connect() {
        // NOP
    }

    /**
     * Disconnects from the SQLDatabase
     */
    @Override
    public final void disconnect() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_DISCONNECT, this.hostName, String.valueOf(this.port), this.name, this.user);
        try {
            this.executor.shutdown();
            this.dataSource.close();
            storageAction.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
    }

    /**
     * Executes an SQL Statement
     * @param sql The SQL statement
     * @param params The params for the Statement
     * @deprecated {@link SQLDatabase#execute(String, WrappedParameters)}
     */
    @Deprecated
    public void execute(final String sql, final Map<Integer, Object> params) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTE, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    SQLDatabase.this.populatePreparedStatement(ps, params);
                    ps.execute();
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    storageAction.setSuccess(false);
                }
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Executes an SQL Statement
     * @param sql The SQL statement
     * @param params The params for the Statement
     */
    public void execute(final String sql, final WrappedParameters params) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTE, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    params.populate(ps);
                    ps.execute();
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    storageAction.setSuccess(false);
                }
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Executes an SQL Statement update
     * @param sql The SQL statement
     * @param params The params for the Statement
     * @deprecated {@link SQLDatabase#executeUpdate(String, WrappedParameters)}
     */
    @Deprecated
    public void executeUpdate(final String sql, final Map<Integer, Object> params) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTEUPDATE, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    SQLDatabase.this.populatePreparedStatement(ps, params);
                    ps.executeUpdate();
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    storageAction.setSuccess(false);
                }
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Executes an SQL Statement update
     * @param sql The SQL statement
     * @param params The params for the Statement
     */
    public void executeUpdate(final String sql, final WrappedParameters params) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTEUPDATE, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    params.populate(ps);
                    ps.executeUpdate();
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    storageAction.setSuccess(false);
                }
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Executes an SQL Statement query
     * @param sql The SQL statement
     * @param params The params for the Statement
     * @param columns The columns to query
     * @param callbackHandler The CallbackHandler to call back to
     * @deprecated {@link SQLDatabase#executeQuery(String, WrappedParameters, CallbackHandler)}
     */
    @Deprecated
    public void executeQuery(final String sql, final Map<Integer, Object> params, final Map<String, Class> columns, final CallbackHandler<List<Map<String, Object>>> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTEQUERY, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> results = new ArrayList<>();
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    SQLDatabase.this.populatePreparedStatement(ps, params);
                    ResultSet resultSet = ps.executeQuery();
                    while (resultSet.next()) {
                        results.add(SQLDatabase.this.getColumns(resultSet, columns));
                    }
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    storageAction.setSuccess(false);
                }
                callbackHandler.callback(results);
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Executes an SQL Statement query
     * @param sql The SQL statement
     * @param params The params for the Statement
     * @param callbackHandler The CallbackHandler to call back to
     */
    public void executeQuery(final String sql, final WrappedParameters params, final CallbackHandler<WrappedResultSet> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_EXECUTEQUERY, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                WrappedResultSet wrappedResultSet;
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    params.populate(ps);
                    wrappedResultSet = new WrappedResultSet(ps.executeQuery());
                    storageAction.setSuccess(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    wrappedResultSet = new WrappedResultSet();
                    storageAction.setSuccess(false);
                }
                callbackHandler.callback(wrappedResultSet);
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Checks whether a ResultSet has entries
     * @param sql The SQL statement
     * @param params The params for the Statement
     * @param callbackHandler The CallbackHandler to call back to
     * @deprecated
     */
    @Deprecated
    public void hasResultSetNext(final String sql, final Map<Integer, Object> params, final CallbackHandler<Boolean> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.SQL_HASRESULTSETNEXT, sql);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection c = SQLDatabase.this.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(sql);
                    SQLDatabase.this.populatePreparedStatement(ps, params);
                    ResultSet resultSet = ps.executeQuery();
                    callbackHandler.callback(resultSet.next());
                } catch (Exception e) {
                    e.printStackTrace();
                    callbackHandler.callback(false);
                }
                SQLDatabase.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Populates a PreparedStatement
     * @param ps The PreparedStatement
     * @param params The parameter map
     * @throws SQLException When an SQLException occurs
     */
    private void populatePreparedStatement(PreparedStatement ps, Map<Integer, Object> params) throws SQLException {
        for(Map.Entry<Integer, Object> entry : params.entrySet()) {
            Object o = entry.getValue();
            if (o instanceof String) ps.setString(entry.getKey(), (String) o);
            else if (o instanceof Integer) ps.setInt(entry.getKey(), (Integer) o);
            else if (o instanceof Boolean) ps.setBoolean(entry.getKey(), (Boolean) o);
            else throw new UnsupportedOperationException("unsupported type " + o.getClass().getSimpleName());
        }
    }

    /**
     * Returns the columns from a ResultSet
     * @param rs The ResultSet
     * @param columns The columns to return
     * @return The columns
     * @throws SQLException When an SQLException occurs
     */
    private Map<String, Object> getColumns(ResultSet rs, Map<String, Class> columns) throws SQLException {
        Map<String, Object> values = new HashMap<>();
        for(Map.Entry<String, Class> entry : columns.entrySet()) {
            String columnLabel = entry.getKey();
            Class c = entry.getValue();
            if (c == String.class) values.put(columnLabel, rs.getString(entry.getKey()));
            else if (c == Integer.class) values.put(columnLabel, rs.getInt(entry.getKey()));
            else if (c == Boolean.class) values.put(columnLabel, rs.getInt(entry.getKey()));
            else throw new UnsupportedOperationException("unsupported type " + c.getSimpleName());
        }
        return values;
    }
}
