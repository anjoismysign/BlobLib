package us.mytheria.bloblib.utilities;

import me.anjoismysign.anjo.crud.CrudManagerBuilder;
import me.anjoismysign.anjo.crud.Crudable;
import me.anjoismysign.anjo.crud.MySQLCrudManager;
import me.anjoismysign.anjo.crud.SQLiteCrudManager;
import me.anjoismysign.anjo.entities.NamingConventions;
import org.bukkit.configuration.ConfigurationSection;
import us.mytheria.bloblib.managers.BlobPlugin;

import java.util.UUID;
import java.util.function.Function;

public class BlobCrudManagerBuilder {

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
    public static <T extends Crudable> MySQLCrudManager<T> MYSQL(BlobPlugin plugin,
                                                                 String primaryKeyName, int primaryKeyLength,
                                                                 String crudableName, Function<String, T> createFunction,
                                                                 boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
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

    public static <T extends Crudable> MySQLCrudManager<T> MYSQL(BlobPlugin plugin,
                                                                 String primaryKeyName, int primaryKeyLength,
                                                                 String crudableName, Function<String, T> createFunction) {
        return MYSQL(plugin, primaryKeyName, primaryKeyLength,
                crudableName, createFunction, false);
    }

    public static <T extends Crudable> MySQLCrudManager<T> MYSQL_UUID(BlobPlugin plugin,
                                                                      String primaryKeyName, int primaryKeyLength,
                                                                      String crudableName, Function<UUID, T> createFunction,
                                                                      boolean logActivity) {
        return MYSQL(plugin, primaryKeyName, primaryKeyLength,
                crudableName, string -> {
                    UUID uuid = UUID.fromString(string);
                    return createFunction.apply(uuid);
                }, logActivity);
    }

    /**
     * Creates a new SQLiteCrudManager in which the primary key is a UUID.
     * Will attempt to load from the plugin's config file.
     * It should have a ConfigurationSection named "Database"
     * with the following keys:
     * - Hostname (a String)
     * - Port (an Integer)
     * - Database (a String)
     * - Username (a String)
     * - Password (a String)
     * <p>
     * logActivity is set to false,
     * which means it will not log activity.
     * related to the database.
     *
     * @param plugin           The plugin to load the config from.
     * @param primaryKeyName   The name of the primary key.
     * @param primaryKeyLength The length of the primary key.
     * @param crudableName     The name of the crudable.
     * @param createFunction   The function to create a new instance of the crudable.
     * @param <T>              The type of crudable.
     * @return A new SQLiteCrudManager.
     */
    public static <T extends Crudable> MySQLCrudManager<T> MYSQL_UUID_NO_LOGGER(BlobPlugin plugin,
                                                                                String primaryKeyName, int primaryKeyLength,
                                                                                String crudableName, Function<UUID, T> createFunction) {
        return MYSQL_UUID(plugin, primaryKeyName, primaryKeyLength,
                crudableName, createFunction, false);
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
    public static <T extends Crudable> SQLiteCrudManager<T> SQLITE(BlobPlugin plugin,
                                                                   String primaryKeyName, int primaryKeyLength,
                                                                   String crudableName, Function<String, T> function,
                                                                   boolean logActivity) {
        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("Database");
        String database = databaseSection.getString("Database");
        if (logActivity)
            return CrudManagerBuilder.SQLITE(database, plugin.getDataFolder(), tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(), function, plugin.getAnjoLogger());
        else
            return CrudManagerBuilder.SQLITE_NO_LOGGER(database, plugin.getDataFolder(), tableNamingConvention(crudableName), primaryKeyName,
                    primaryKeyLength, crudableName.toUpperCase(), function);
    }

    public static <T extends Crudable> SQLiteCrudManager<T> SQLITE_UUID(BlobPlugin plugin,
                                                                        String primaryKeyName, int primaryKeyLength,
                                                                        String crudableName, Function<UUID, T> function,
                                                                        boolean logActivity) {
        return SQLITE(plugin, primaryKeyName, primaryKeyLength, crudableName, string -> {
            UUID uuid = UUID.fromString(string);
            return function.apply(uuid);
        }, logActivity);
    }

    /**
     * Creates a new SQLiteCrudManager in which the primary key is a UUID.
     * Will attempt to load from the plugin's config file.
     * It should have a ConfigurationSection named "Database"
     * with the following key:
     * - Database (a String)
     * <p>
     * logActivity is set to false,
     * which means it will not log activity.
     * related to the database.
     *
     * @param plugin           The plugin to load the config from.
     * @param primaryKeyName   The name of the primary key.
     * @param primaryKeyLength The length of the primary key.
     * @param crudableName     The name of the crudable.
     * @param function         The function to create a new instance of the crudable.
     * @param <T>              The type of crudable.
     * @return A new SQLiteCrudManager.
     */
    public static <T extends Crudable> SQLiteCrudManager<T> SQLITE_UUID_NO_LOGGER(BlobPlugin plugin,
                                                                                  String primaryKeyName, int primaryKeyLength,
                                                                                  String crudableName, Function<UUID, T> function) {
        return SQLITE_UUID(plugin, primaryKeyName, primaryKeyLength, crudableName, function, false);
    }

    private static String tableNamingConvention(String name) {
        name = NamingConventions.toPascalCase(name);
        name = "tbl" + name + "s";
        return name;
    }
}
