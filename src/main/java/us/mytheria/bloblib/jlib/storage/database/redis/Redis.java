package us.mytheria.bloblib.jlib.storage.database.redis;

import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import us.mytheria.bloblib.jlib.storage.StorageAction;
import us.mytheria.bloblib.jlib.storage.database.CallbackHandler;
import us.mytheria.bloblib.jlib.storage.database.Database;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public final class Redis extends Database {
    private Jedis jedis;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * Constructs a new Redis instance, shouldn't be used externally, use {@link RedisLoader} instead
     * @param plugin The JavaPlugin associated with the Redis Database
     * @param hostName The host name of the Redis Server
     * @param port The port of the Redis Server
     * @param password The password to use
     */
    Redis(JavaPlugin plugin, String hostName, int port, String password) {
        super(plugin, hostName, port, "Redis Database", null, password);
    }

    /**
     * Connects to the Redis Database
     */
    @Override
    public void connect() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_CONNECT, this.hostName, String.valueOf(this.port), this.name);
        try {
            this.jedis = this.getConnection();
            storageAction.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
    }

    /**
     * Disconnects from the Redis Database
     */
    @Override
    public void disconnect() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_DISCONNECT, this.hostName, String.valueOf(this.port), this.name);
        try {
            this.executor.shutdown();
            this.jedis.close();
            storageAction.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
    }

    /**
     * Returns the Connection for the Redis Database
     * @return The Connection
     */
    private Jedis getConnection() {
        Jedis j = new Jedis(this.hostName, this.port);
        j.auth(this.password);
        return j;
    }

    /**
     * Sets a Key to a value
     * @param key The Key to set
     * @param value The value to set to the Key
     */
    @SuppressWarnings("deprecation")
    public void set(final String key, final String value) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_SET, key, value);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                Redis.this.jedis.set(key, value);
                storageAction.setSuccess(true);
                Redis.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Sets Keys to values
     * @param keysvalues The Keys and values to set
     */
    @SuppressWarnings("deprecation")
    public void set(final String... keysvalues) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_SETMULTIPLE, Arrays.toString(keysvalues));
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                Redis.this.jedis.mset(keysvalues);
                storageAction.setSuccess(true);
                Redis.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Returns a value from a Key
     * @param key The Key of which to get the value
     * @return The value
     * @deprecated {@link Redis#get(String, CallbackHandler)}
     */
    @Deprecated
    public String get(String key) {
        this.jLogger.deprecation();
        this.jLogger.warnIfSync();
        StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_GET, key);
        storageAction.setSuccess(true);
        this.actions.add(storageAction);
        return this.jedis.get(key);
    }

    /**
     * Returns a value from a Key
     * @param key The Key of which to get the value
     * @param callbackHandler The Callback Handler
     */
    @SuppressWarnings("deprecation")
    public void get(final String key, final CallbackHandler<String> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_GET, key);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                callbackHandler.callback(Redis.this.jedis.get(key));
                storageAction.setSuccess(true);
                Redis.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Returns all the values of the Keys
     * @param keys They keys of which to get the values
     * @return The values
     * @deprecated {@link Redis#get(CallbackHandler, String...)}
     */
    @Deprecated
    public List<String> get(String... keys) {
        this.jLogger.deprecation();
        this.jLogger.warnIfSync();
        StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_GETMULTIPLE, Arrays.toString(keys));
        storageAction.setSuccess(true);
        this.actions.add(storageAction);
        return this.jedis.mget(keys);
    }

    /**
     * Returns a value from a Key
     * @param callbackHandler The Callback Handler
     * @param keys They keys of which to get the values
     */
    @SuppressWarnings("deprecation")
    public void get(final CallbackHandler<List<String>> callbackHandler, final String... keys) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_GETMULTIPLE, Arrays.toString(keys));
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                callbackHandler.callback(Redis.this.jedis.mget(keys));
                storageAction.setSuccess(true);
                Redis.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Returns whether a Key exists
     * @param key The Key to check
     * @return Whether the Key exists
     * @deprecated {@link Redis#exists(String, CallbackHandler)}
     */
    @Deprecated
    public boolean exists(String key) {
        this.jLogger.deprecation();
        this.jLogger.warnIfSync();
        StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_EXISTS, key);
        storageAction.setSuccess(true);
        this.actions.add(storageAction);
        return this.jedis.exists(key);
    }

    /**
     * Returns whether a Key exists
     * @param key The Key to check
     * @param callbackHandler The Callback Handler
     */
    @SuppressWarnings("deprecation")
    public void exists(final String key, final CallbackHandler<Boolean> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.REDIS_EXISTS, key);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                callbackHandler.callback(Redis.this.jedis.exists(key));
                storageAction.setSuccess(true);
                Redis.this.actions.add(storageAction);
            }
        });
    }
}
