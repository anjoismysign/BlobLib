package io.github.anjoismysign.bloblib.utilities;

import io.github.anjoismysign.bloblib.entities.BlobCrudable;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.storage.BlobCrudManager;
import io.github.anjoismysign.bloblib.storage.IdentifierType;
import io.github.anjoismysign.bloblib.storage.MongoCrudManager;
import io.github.anjoismysign.bloblib.storage.MongoDB;
import io.github.anjoismysign.bloblib.storage.StorageType;
import io.github.anjoismysign.anjo.crud.CrudManagerBuilder;
import io.github.anjoismysign.anjo.crud.Crudable;
import io.github.anjoismysign.anjo.crud.MySQLCrudManager;
import io.github.anjoismysign.anjo.crud.SQLiteCrudManager;
import io.github.anjoismysign.anjo.entities.NamingConventions;
import io.github.anjoismysign.anjo.entities.Result;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

/**
 * It will create a new BlobCrudManager for you.
 * The easiest way to use this is to call the PLAYER method.
 */
public class BlobCrudManagerFactory {
    public static <T extends BlobCrudable> BlobCrudManager<T> PLAYER(BlobPlugin plugin,
                                                                     String crudableName,
                                                                     Function<BlobCrudable, T> createFunction,
                                                                     boolean logActivity) {
        IdentifierType identifierType;
        String type = plugin.getConfig().getString("IdentifierType", "UUID");
        try {
            identifierType = IdentifierType.valueOf(type);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid IdentifierType '" + type + "'. Defaulting to UUID.");
            identifierType = IdentifierType.UUID;
        }
        FileConfiguration config = plugin.getConfig();
        if (!config.isConfigurationSection("Database")) {
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database'.");
        }
        ConfigurationSection databaseSection = config.getConfigurationSection("Database");
        if (!databaseSection.isString("Hostname"))
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database.Hostname'.");
        if (!databaseSection.isInt("Port"))
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database.Port'.");
        if (!databaseSection.isString("Database"))
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database.Database'.");
        if (!databaseSection.isString("Username"))
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database.Username'.");
        if (!databaseSection.isString("Password"))
            throw new IllegalArgumentException("Invalid database configuration. Missing 'Database.Password'.");
        switch (identifierType) {
            case UUID -> {
                return UUID(plugin, crudableName, uuid -> {
                    BlobCrudable crudable = new BlobCrudable(uuid.toString());
                    return createFunction.apply(crudable);
                }, logActivity);
            }
            case PLAYERNAME -> {
                return PLAYERNAME(plugin, crudableName, playerName -> {
                    BlobCrudable crudable = new BlobCrudable(playerName);
                    return createFunction.apply(crudable);
                }, logActivity);
            }
            default -> throw new IllegalArgumentException("Invalid IdentifierType '" + identifierType + "'.");
        }
    }

    public static <T extends BlobCrudable> BlobCrudManager<T> UUID(BlobPlugin plugin,
                                                                   String crudableName, Function<UUID, T> createFunction,
                                                                   boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        StorageType storageType;
        String type = databaseSection.getString("Type", "SQLITE");
        try {
            storageType = StorageType.valueOf(type);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid StorageType '" + type + "'. Defaulting to SQLITE.");
            storageType = StorageType.SQLITE;
        }
        switch (storageType) {
            case MYSQL -> {
                return new BlobCrudManager<>(MYSQL(plugin, "UUID", 36,
                        crudableName, string -> {
                            UUID uuid = UUID.fromString(string);
                            return createFunction.apply(uuid);
                        }, logActivity), StorageType.MYSQL, IdentifierType.UUID);
            }
            case SQLITE -> {
                return new BlobCrudManager<>(SQLITE(plugin, "UUID", 36,
                        crudableName, string -> {
                            UUID uuid = UUID.fromString(string);
                            return createFunction.apply(uuid);
                        }, logActivity), StorageType.SQLITE, IdentifierType.UUID);
            }
            case MONGODB -> {
                return new BlobCrudManager<>(MONGO(plugin, crudableName, string -> {
                    UUID uuid = UUID.fromString(string);
                    return createFunction.apply(uuid);
                }, logActivity), StorageType.MONGODB, IdentifierType.UUID);
            }
            default -> throw new IllegalArgumentException("Invalid StorageType '" + storageType + "'.");
        }
    }

    public static <T extends BlobCrudable> BlobCrudManager<T> PLAYERNAME(BlobPlugin plugin,
                                                                         String crudableName, Function<String, T> createFunction,
                                                                         boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        StorageType storageType;
        String type = databaseSection.getString("Type", "SQLITE");
        try {
            storageType = StorageType.valueOf(type);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid StorageType '" + type + "'. Defaulting to SQLITE.");
            storageType = StorageType.SQLITE;
        }
        switch (storageType) {
            case MYSQL -> {
                return new BlobCrudManager<>(MYSQL(plugin, "USERNAME", 16,
                        crudableName, createFunction, logActivity), StorageType.MYSQL, IdentifierType.PLAYERNAME);
            }
            case SQLITE -> {
                return new BlobCrudManager<>(SQLITE(plugin, "USERNAME", 16,
                        crudableName, createFunction, logActivity), StorageType.SQLITE, IdentifierType.PLAYERNAME);
            }
            case MONGODB -> {
                return new BlobCrudManager<>(MONGO(plugin, crudableName, createFunction, logActivity),
                        StorageType.MONGODB, IdentifierType.PLAYERNAME);
            }
            default -> throw new IllegalArgumentException("Invalid StorageType '" + storageType + "'.");
        }
    }

    /**
     * Creates a new SQLiteCrudManager.
     * Will attempt to load from the plugin's config file.
     * It should have a ConfigurationSection named "Database"
     * with the following keys:
     * - Hostname (a String)
     * - Port (an Integer)
     * - Database (a String)
     * - Username (a String)
     * - Password (a String)
     *
     * @param plugin           The plugin to load the config from.
     * @param primaryKeyName   The name of the primary key.
     * @param primaryKeyLength The length of the primary key.
     * @param crudableName     The name of the crudable.
     * @param createFunction   The function to create a new instance of the crudable.
     * @param logActivity      Whether to log activity.
     * @param <T>              The type of crudable.
     * @return A new SQLiteCrudManager.
     */
    private static <T extends Crudable> MySQLCrudManager<T> MYSQL(BlobPlugin plugin,
                                                                  String primaryKeyName, int primaryKeyLength,
                                                                  String crudableName, Function<String, T> createFunction,
                                                                  boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        StorageType storageType = StorageType.valueOf(databaseSection.getString("Type", "SQLITE"));
        if (storageType != StorageType.MYSQL)
            throw new IllegalArgumentException("StorageType is not MYSQL (" + storageType + ")");
        String hostname = databaseSection.getString("Hostname");
        int port = databaseSection.getInt("Port");
        String database = databaseSection.getString("Database");
        String user = databaseSection.getString("Username");
        String password = databaseSection.getString("Password");
        if (logActivity)
            return CrudManagerBuilder.MYSQL(hostname, port, database, user, password, tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(Locale.ROOT), createFunction, plugin.getAnjoLogger());
        else
            return CrudManagerBuilder.MYSQL_NO_LOGGER(hostname, port, database, user, password, tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(Locale.ROOT), createFunction);
    }


    /**
     * Creates a new SQLiteCrudManager.
     * Will attempt to load from the plugin's config file.
     * It should have a ConfigurationSection named "Database"
     * with the following key:
     * - Database (a String)
     *
     * @param plugin           The plugin to load the config from.
     * @param primaryKeyName   The name of the primary key.
     * @param primaryKeyLength The length of the primary key.
     * @param crudableName     The name of the crudable.
     * @param function         The function to create a new instance of the crudable.
     * @param logActivity      Whether to log activity.
     * @param <T>              The type of crudable.
     * @return A new SQLiteCrudManager.
     */
    private static <T extends Crudable> SQLiteCrudManager<T> SQLITE(BlobPlugin plugin,
                                                                    String primaryKeyName, int primaryKeyLength,
                                                                    String crudableName, Function<String, T> function,
                                                                    boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        StorageType storageType = StorageType.valueOf(databaseSection.getString("Type", "SQLITE"));
        if (storageType != StorageType.SQLITE)
            throw new IllegalArgumentException("StorageType is not SQLITE (" + storageType + ")");
        String database = databaseSection.getString("Database");
        if (logActivity)
            return CrudManagerBuilder.SQLITE(database, plugin.getDataFolder(), tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(Locale.ROOT), function, plugin.getAnjoLogger());
        else
            return CrudManagerBuilder.SQLITE_NO_LOGGER(database, plugin.getDataFolder(), tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(Locale.ROOT), function);
    }

    public static String tableNamingConvention(String name) {
        name = NamingConventions.toPascalCase(name);
        name = "tbl" + name + "s";
        return name;
    }

    private static <T extends BlobCrudable> MongoCrudManager<T> MONGO(BlobPlugin plugin,
                                                                      String crudableName, Function<String, T> createFunction,
                                                                      boolean logActivity) {
        if (!plugin.getConfig().isConfigurationSection("Database"))
            throw new IllegalArgumentException("Database section not found");
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        StorageType storageType = StorageType.valueOf(databaseSection.getString("Type", "SQLITE"));
        if (storageType != StorageType.MONGODB)
            throw new IllegalArgumentException("StorageType is not MONGODB (" + storageType + ")");
        MongoDB mongoDB;
        if (databaseSection.isString("URI")) {
            String uri = databaseSection.getString("URI");
            if (!databaseSection.isString("Database"))
                throw new ConfigurationFieldException("'Database' field not found");
            String database = databaseSection.getString("Database");
            mongoDB = MongoDB.fromURI(uri, database);
        } else {
            Result<MongoDB> result = MongoDB.fromConfigurationSection(databaseSection);
            if (!result.isValid())
                throw new IllegalArgumentException("Invalid MongoDB configuration");
            mongoDB = result.value();
        }
        if (logActivity)
            return new MongoCrudManager<>(mongoDB, NamingConventions.toSnakeCase(crudableName),
                    createFunction, plugin.getAnjoLogger());
        else
            return new MongoCrudManager<>(mongoDB, NamingConventions.toSnakeCase(crudableName),
                    createFunction, null);
    }
}
