package us.mytheria.bloblib.jlib.storage.database.mysql;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.methods.General;
import us.mytheria.bloblib.jlib.storage.StorageAction;
import us.mytheria.bloblib.jlib.storage.database.SQLDatabase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since Unknown
 */
public final class MySQL extends SQLDatabase {

    /**
     * Constructs a new MySQL instance, shouldn't be used externally, use {@link MySQLLoader} instead
     * @param plugin The JavaPlugin associated with the MySQL Database
     * @param hostName The host name of the MySQL Server
     * @param port The port of the MySQL Server
     * @param database The name of the MySQL Database
     * @param user The user to use
     * @param password The password to use
     */
    public MySQL(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(plugin, hostName, port, database, user, password);
        this.dataSource.setJdbcUrl("jdbc:mysql://" + this.hostName + ':' + this.port + '/' + this.name);
    }

    /**
     * Returns the Connection for the MySQL Database
     * @return The Connection
     * @see Connection
     */
    @Override
    public Connection getConnection() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.MYSQL_GETCONNECTION, this.hostName, String.valueOf(this.port), this.name, this.user);
        Connection c = null;
        try {
            c = this.dataSource.getConnection();
            storageAction.setSuccess(true);
        } catch (SQLException e) {
            e.printStackTrace();
            General.sendColoredMessage(this.plugin, "Failed to connect to the MySQL Database using following credentials:", ChatColor.RED);
            General.sendColoredMessage(this.plugin, "HostName: " + this.hostName, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Port: " + this.port, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Database: " + this.name, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "User: " + this.user, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Password: =REDACTED=", ChatColor.GOLD);
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
        return c;
    }
}
