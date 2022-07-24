package us.mytheria.bloblib.jlib.storage.database.mongodb;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.StorageLoader;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public abstract class MongoDBLoader extends StorageLoader {
    protected final MongoDB mongoDB;

    /**
     * Constructs a new MongoDBLoader, use this by extending the MongoDBLoader
     * @param plugin The JavaPlugin associated with the MongoDB Database
     * @param hostName The host name of the MongoDB Server
     * @param port The port of the MongoDB Server
     * @param database The name of the MongoDB Database
     * @param user The user to use
     * @param password The password to use
     * @see MongoDB
     */
    protected MongoDBLoader(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(new MongoDB(plugin, hostName, port, database, user, password));
        this.mongoDB = (MongoDB) this.storage;
        this.mongoDB.connect();
    }
}
