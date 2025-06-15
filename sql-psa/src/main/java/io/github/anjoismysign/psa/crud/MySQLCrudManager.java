package io.github.anjoismysign.psa.crud;

import io.github.anjoismysign.psa.UpdatableSerializable;
import io.github.anjoismysign.psa.UpdatableSerializableHandler;
import io.github.anjoismysign.psa.sql.MySQLCrudDatabase;
import io.github.anjoismysign.psa.sql.SQLContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MySQLCrudManager<T extends Crudable> implements SQLCrudManager<T> {
    private final Function<String, T> createFunction;
    private final MySQLCrudDatabase credentials;
    private SQLContainer container;

    public MySQLCrudManager(@NotNull MySQLCrudDatabase credentials, @NotNull Function<String, T> createFunction) {
        this.createFunction = createFunction;
        this.credentials = credentials;
        this.load();
    }

    public void load() {
        this.container = this.credentials.generateContainer();
        boolean isNewTable = this.container
                .getDatabase()
                .createTable(
                        this.getTableName(),
                        this.getPrimaryKeyName() + " VARCHAR(" + this.getPrimaryKeyLength() + ")," + this.getCrudableKeyTypeName() + " BLOB",
                        this.getPrimaryKeyName()
                );
        if (isNewTable) {
            this.log(
                    "Create table "
                            + this.getTableName()
                            + " with primary key "
                            + this.getPrimaryKeyName()
                            + "and type "
                            + this.getCrudableKeyTypeName()
                            + " was executed successfully."
            );
        }
    }

    public String getCrudableKeyTypeName() {
        return this.credentials.getCrudableKeyTypeName();
    }

    public String getTableName() {
        return this.credentials.getTableName();
    }

    public String getPrimaryKeyName() {
        return this.credentials.getPrimaryKeyName();
    }

    public int getPrimaryKeyLength() {
        return this.credentials.getPrimaryKeyLength();
    }

    public Connection getConnection() {
        return this.container.getDatabase().getConnection();
    }

    public boolean exists(String id) {
        boolean exists = this.container.getDatabase().exists(this.getTableName(), this.getPrimaryKeyName(), id);
        if (exists) {
            this.log("Record with id " + id + " exists.");
        } else {
            this.log("Record with id " + id + " does not exist.");
        }

        return exists;
    }

    public void update(T crudable, int version) {
        UpdatableSerializableHandler<T> handler = this.newUpdatable(crudable, 0);
        String id = crudable.getIdentification();
        PreparedStatement statement = this.container
                .getDatabase()
                .updateDataSet(this.getPrimaryKeyName(), this.getTableName(), this.crudableKeyTypePrepareStatement());

        try {
            statement.setBytes(1, handler.serialize());
            statement.setString(2, id);
            statement.executeUpdate();
            if (version != 0) {
                this.log("Updated record with id " + id + " to version " + version + ".");
            } else {
                this.log("Updated record with id " + id + ".");
            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        } finally {
            try {
                statement.close();
                statement.getConnection().close();
            } catch (SQLException var14) {
                var14.printStackTrace();
            }
        }
    }

    public void forEachRecord(BiConsumer<T, Integer> biConsumer) {
        this.container.getDatabase().selectAllFromDatabase(this.getTableName(), resultSet -> {
            try {
                byte[] bytes = resultSet.getBytes(this.getCrudableKeyTypeName());
                UpdatableSerializable<T> updatableSerializable = UpdatableSerializable.deserialize(bytes);
                T crudable = updatableSerializable.getValue();
                this.log("Read record with id " + crudable.getIdentification() + ".");
                biConsumer.accept(crudable, updatableSerializable.getVersion());
            } catch (SQLException var6) {
                var6.printStackTrace();
            }
        });
    }

    public void reload() {
        this.log("Reloading database...");
        this.container.disconnect();
        this.load();
    }

    public T create(String identification) {
        T crudable = this.createFunction.apply(identification);
        Connection connection = null;
        String sql = "INSERT IGNORE INTO " + this.getTableName();

        try {
            connection = this.container.getDatabase().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql + " (" + this.getPrimaryKeyName() + ") VALUES (?)");
            if (!this.exists(identification)) {
                preparedStatement.setString(1, identification);
                preparedStatement.executeUpdate();
                this.log("Created new record with id " + identification + ".");
            }

            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement.getConnection().close();
            }
        } catch (SQLException var14) {
            var14.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException var13) {
                    var13.printStackTrace();
                }
            }
        }

        return crudable;
    }

    @NotNull
    public T read(String id) {
        return this.readOrGenerate(id, () -> this.create(id));
    }

    @Nullable
    public T readOrNull(String id) {
        return this.readOrGenerate(id, () -> null);
    }

    public void update(T crudable) {
        this.update(crudable, 0);
    }

    public void delete(String id) {
        PreparedStatement preparedStatement = this.container.getDatabase().delete(this.getTableName(), this.getPrimaryKeyName());

        try {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            this.log("Deleted record with id " + id + ".");
        } catch (SQLException var12) {
            var12.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                preparedStatement.getConnection().close();
            } catch (SQLException var11) {
                var11.printStackTrace();
            }
        }
    }

    @Nullable
    public Logger getLogger() {
        return this.credentials.getLogger();
    }

    private T readOrGenerate(String id, Supplier<T> replacement) {
        ResultSet resultSet = this.container.getDatabase().selectRowByPrimaryKey(this.getPrimaryKeyName(), id, this.getTableName());

        Crudable var7;
        try {
            if (!resultSet.next()) {
                this.log("Record with id " + id + " does not exist.");
                resultSet.close();
                resultSet.getStatement().close();
                resultSet.getStatement().getConnection().close();
                return replacement.get();
            }

            byte[] bytes = resultSet.getBytes(this.getCrudableKeyTypeName());
            resultSet.close();
            resultSet.getStatement().close();
            resultSet.getStatement().getConnection().close();
            UpdatableSerializable<T> updatableSerializable = UpdatableSerializable.deserialize(bytes);
            T crudable = updatableSerializable.getValue();
            this.log("Read record with id " + id + " successfully.");
            var7 = crudable;
        } catch (SQLException var18) {
            var18.printStackTrace();
            return replacement.get();
        } finally {
            try {
                resultSet.getStatement().close();
                resultSet.getStatement().getConnection().close();
            } catch (SQLException var17) {
                var17.printStackTrace();
            }
        }

        return (T) var7;
    }

    private void log(@NotNull String message) {
        Logger logger = this.getLogger();
        if (logger != null) {
            logger.info(message);
        }
    }
}
