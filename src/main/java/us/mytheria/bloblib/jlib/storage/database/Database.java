package us.mytheria.bloblib.jlib.storage.database;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.Storage;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public abstract class Database extends Storage {
    protected final String hostName;
    protected final int port;
    protected final String user;
    protected final String password;

    /**
     * Constructs a new Database instance, shouldn't be used externally
     * @param plugin The JavaPlugin associated with the Database
     * @param hostName The host name of the Server
     * @param port The port of the Server
     * @param database The name of the Database
     * @param user The user to use
     * @param password The password to use
     */
    protected Database(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(plugin, database);
        this.hostName = hostName;
        this.port = port;
        this.user = user;
        this.password = password;
    }
}
