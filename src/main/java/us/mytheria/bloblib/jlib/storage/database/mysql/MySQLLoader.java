package us.mytheria.bloblib.jlib.storage.database.mysql;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.database.SQLLoader;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public class MySQLLoader extends SQLLoader {

    /**
     * Constructs a new MySQLLoader, use this by extending the MySQLLoader
     * @param plugin The JavaPlugin associated with the MySQL Database
     * @param hostName The host name of the MySQL Server
     * @param port The port of the MySQL Server
     * @param database The name of the MySQL Database
     * @param user The user to use
     * @param password The password to use
     * @see MySQL
     */
    public MySQLLoader(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(new MySQL(plugin, hostName, port, database, user, password));
    }
}
