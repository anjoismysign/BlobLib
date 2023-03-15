package us.mytheria.bloblib.utilities;

import me.anjoismysign.anjo.crud.*;
import me.anjoismysign.anjo.entities.NamingConventions;
import me.anjoismysign.anjo.entities.Result;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.entities.BlobCrudable;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.storage.IdentifierType;
import us.mytheria.bloblib.storage.MongoCrudManager;
import us.mytheria.bloblib.storage.MongoDB;
import us.mytheria.bloblib.storage.StorageType;

import java.util.UUID;
import java.util.function.Function;

public class BlobCrudManagerBuilder {
    public static <T extends BlobCrudable> CrudManager<T> PLAYER(BlobPlugin plugin,
                                                                 String crudableName,
                                                                 Function<Player, T> createFunction,
                                                                 boolean logActivity) {
        IdentifierType identifierType;
        String type = plugin.getConfig().getString("IdentifierType", "UUID");
        try {
            identifierType = IdentifierType.valueOf(type);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid IdentifierType '" + type + "'. Defaulting to UUID.");
            identifierType = IdentifierType.UUID;
        }
        switch (identifierType) {
            case UUID -> {
                return UUID(plugin, crudableName, uuid -> {
                    Player player = plugin.getServer().getPlayer(uuid);
                    if (player == null) return null;
                    return createFunction.apply(player);
                }, logActivity);
            }
            case PLAYERNAME -> {
                return PLAYERNAME(plugin, crudableName, playerName -> {
                    Player player = plugin.getServer().getPlayer(playerName);
                    if (player == null) return null;
                    return createFunction.apply(player);
                }, logActivity);
            }
            default -> throw new IllegalArgumentException("Invalid IdentifierType '" + identifierType + "'.");
        }
    }

    public static <T extends BlobCrudable> CrudManager<T> UUID(BlobPlugin plugin,
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
        String hostname = databaseSection.getString("Hostname");
        int port = databaseSection.getInt("Port");
        String database = databaseSection.getString("Database");
        String user = databaseSection.getString("Username");
        String password = databaseSection.getString("Password");
        switch (storageType) {
            case MYSQL -> {
                return MYSQL(plugin, "UUID", 36,
                        crudableName, string -> {
                            UUID uuid = UUID.fromString(string);
                            return createFunction.apply(uuid);
                        }, logActivity);
            }
            case SQLITE -> {
                return SQLITE(plugin, "UUID", 36,
                        crudableName, string -> {
                            UUID uuid = UUID.fromString(string);
                            return createFunction.apply(uuid);
                        }, logActivity);
            }
            case MONGODB -> {
                return MONGO(plugin, crudableName, string -> {
                    UUID uuid = UUID.fromString(string);
                    return createFunction.apply(uuid);
                }, logActivity);
            }
            default -> throw new IllegalArgumentException("Invalid StorageType '" + storageType + "'.");
        }
    }

    public static <T extends BlobCrudable> CrudManager<T> PLAYERNAME(BlobPlugin plugin,
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
        String hostname = databaseSection.getString("Hostname");
        int port = databaseSection.getInt("Port");
        String database = databaseSection.getString("Database");
        String user = databaseSection.getString("Username");
        String password = databaseSection.getString("Password");
        switch (storageType) {
            case MYSQL -> {
                return MYSQL(plugin, "USERNAME", 36,
                        crudableName, createFunction::apply, logActivity);
            }
            case SQLITE -> {
                return SQLITE(plugin, "USERNAME", 36,
                        crudableName, createFunction::apply, logActivity);
            }
            case MONGODB -> {
                return MONGO(plugin, crudableName, createFunction::apply, logActivity);
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
                    primaryKeyLength, crudableName.toUpperCase(), createFunction, plugin.getAnjoLogger());
        else
            return CrudManagerBuilder.MYSQL_NO_LOGGER(hostname, port, database, user, password, tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(), createFunction);
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
                    primaryKeyLength, crudableName.toUpperCase(), function, plugin.getAnjoLogger());
        else
            return CrudManagerBuilder.SQLITE_NO_LOGGER(database, plugin.getDataFolder(), tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(), function);
    }

    private static String tableNamingConvention(String name) {
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
        Result<MongoDB> result = MongoDB.fromConfigurationSection(databaseSection);
        if (!result.isValid())
            throw new IllegalArgumentException("Invalid MongoDB configuration");
        MongoDB mongoDB = result.value();
        if (logActivity)
            return new MongoCrudManager<>(mongoDB, NamingConventions.toSnakeCase(crudableName),
                    createFunction, plugin.getAnjoLogger());
        else
            return new MongoCrudManager<>(mongoDB, NamingConventions.toSnakeCase(crudableName),
                    createFunction, null);
    }
}
