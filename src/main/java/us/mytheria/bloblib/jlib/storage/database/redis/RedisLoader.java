package us.mytheria.bloblib.jlib.storage.database.redis;

import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.storage.StorageLoader;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public abstract class RedisLoader extends StorageLoader {
    protected final Redis redis;

    /**
     * Constructs a new RedisLoader, use this by extending the RedisLoader
     * @param plugin The JavaPlugin associated with the Redis Database
     * @param hostName The host name of the Redis Server
     * @param port The port of the Redis Server
     * @param password The password to use
     * @see Redis
     */
    protected RedisLoader(JavaPlugin plugin, String hostName, int port, String password) {
        super(new Redis(plugin, hostName, port, password));
        this.redis = (Redis) this.storage;
        this.redis.connect();
    }
}
