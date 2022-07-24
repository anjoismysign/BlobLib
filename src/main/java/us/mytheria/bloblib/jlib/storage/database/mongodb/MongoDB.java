package us.mytheria.bloblib.jlib.storage.database.mongodb;

import com.mongodb.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import us.mytheria.bloblib.jlib.methods.General;
import us.mytheria.bloblib.jlib.storage.StorageAction;
import us.mytheria.bloblib.jlib.storage.database.CallbackHandler;
import us.mytheria.bloblib.jlib.storage.database.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public final class MongoDB extends Database {
    private MongoClient client;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * Constructs a new MongoDB instance, shouldn't be used externally, use {@link MongoDBLoader} instead
     * @param plugin The JavaPlugin associated with the MongoDB Database
     * @param hostName The host name of the MongoDB Server
     * @param port The port of the MongoDB Server
     * @param database The name of the MongoDB Database
     * @param user The user to use
     * @param password The password to use
     */
    MongoDB(JavaPlugin plugin, String hostName, int port, String database, String user, String password) {
        super(plugin, hostName, port, database, user, password);
    }

    /**
     * Connects to the MongoDB Database
     */
    @Override
    public void connect() {
        this.client = this.getConnection();
    }

    /**
     * Disconnects from the MongoDB Database
     */
    @Override
    public void disconnect() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_DISCONNECT, this.hostName, String.valueOf(this.port), this.name, this.user);
        try {
            this.executor.shutdown();
            this.client.close();
            storageAction.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
    }

    /**
     * Returns the MongoClient for the MongoDB Database
     * @return The MongoClient
     */
    private MongoClient getConnection() {
        StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_GETCONNECTION, this.hostName, String.valueOf(this.port), this.name, this.user);
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(new ServerAddress(this.hostName, this.port), Collections.singletonList(MongoCredential.createCredential(this.user, this.name, this.password.toCharArray())));
            storageAction.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            General.sendColoredMessage(this.plugin, "Failed to connect to the MongoDB Database using following credentials:", ChatColor.RED);
            General.sendColoredMessage(this.plugin, "HostName: " + this.hostName, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Port: " + this.port, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Database: " + this.name, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "User: " + this.user, ChatColor.GOLD);
            General.sendColoredMessage(this.plugin, "Password: =REDACTED=", ChatColor.GOLD);
            storageAction.setSuccess(false);
        }
        this.actions.add(storageAction);
        return mongoClient;
    }

    /**
     * Performs a command on the Database
     * @param command The command to perform
     */
    @SuppressWarnings("deprecation")
    public void performCommand(final String command) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_COMMAND, command);
        storageAction.setSuccess(true);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                MongoDB.this.client.getDB(MongoDB.this.name).command(command);
                MongoDB.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Stores an Object in a Collection
     * @param object The Object to store
     * @param collection The Collection to store it in
     * @see DBObject
     */
    @SuppressWarnings("deprecation")
    public void storeObject(final DBObject object, final String collection) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_STORE, Arrays.toString(object.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                MongoDB.this.client.getDB(MongoDB.this.name).getCollection(collection).insert(object);
                MongoDB.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Returns an Object in a Collection, based on a reference Object
     * @param reference The reference Object
     * @param collection The Collection
     * @return The found Object
     * @deprecated {@link MongoDB#getObject(DBObject, String, CallbackHandler)}
     * @see DBObject
     */
    @Deprecated
    public DBObject getObject(DBObject reference, String collection) {
        this.jLogger.deprecation();
        this.jLogger.warnIfSync();
        StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_GET, Arrays.toString(reference.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        this.actions.add(storageAction);
        return this.client.getDB(this.name).getCollection(collection).findOne(reference);
    }

    /**
     * Returns an Object in a Collection, based on a reference Object
     * @param reference The reference Object
     * @param collection The Collection
     * @param callbackHandler The Callback Handler
     * @see DBObject
     */
    @SuppressWarnings("deprecation")
    public void getObject(final DBObject reference, final String collection, final CallbackHandler<DBObject> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_GET, Arrays.toString(reference.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                callbackHandler.callback(MongoDB.this.client.getDB(MongoDB.this.name).getCollection(collection).findOne(reference));
                MongoDB.this.actions.add(storageAction);
            }
        });
    }

    /**
     * Returns multiple Objects in a Collection, based on a reference Object
     * @param reference The reference Object
     * @param collection The Collection
     * @return The found Object
     * @deprecated {@link MongoDB#getObjects(DBObject, String, CallbackHandler)}
     * @see DBObject
     */
    @Deprecated
    public List<DBObject> getObjects(DBObject reference, String collection) {
        this.jLogger.deprecation();
        this.jLogger.warnIfSync();
        StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_GET, Arrays.toString(reference.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        DBCursor cursor = this.client.getDB(this.name).getCollection(collection).find(reference);
        List<DBObject> objects = new ArrayList<>();
        while(cursor.hasNext()) {
            objects.add(cursor.next());
        }
        this.actions.add(storageAction);
        return objects;
    }

    /**
     * Returns multiple Objects in a Collection, based on a reference Object
     * @param reference The reference Object
     * @param collection The Collection
     * @param callbackHandler The Callback Handler
     * @see DBObject
     */
    @SuppressWarnings("deprecation")
    public void getObjects(final DBObject reference, final String collection, final CallbackHandler<List<DBObject>> callbackHandler) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_GET, Arrays.toString(reference.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                DBCursor cursor = MongoDB.this.client.getDB(MongoDB.this.name).getCollection(collection).find(reference);
                List<DBObject> objects = new ArrayList<>();
                while(cursor.hasNext()) {
                    objects.add(cursor.next());
                }
                MongoDB.this.actions.add(storageAction);
                callbackHandler.callback(objects);
            }
        });
    }

    /**
     * Updates an Object in a Collection, based on a reference Object
     * @param object The Object to update
     * @param reference The reference Object
     * @param collection The Collection
     * @see DBObject
     */
    @SuppressWarnings("deprecation")
    public void updateObject(final DBObject object, final DBObject reference, final String collection) {
        final StorageAction storageAction = new StorageAction(StorageAction.Type.MONGO_UPDATE, Arrays.toString(object.toMap().entrySet().toArray()), Arrays.toString(reference.toMap().entrySet().toArray()), collection);
        storageAction.setSuccess(true);
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                MongoDB.this.getObject(reference, collection, new CallbackHandler<DBObject>() {
                    @Override
                    public void callback(DBObject o) {
                        if(o == null) MongoDB.this.storeObject(object, collection);
                        else MongoDB.this.client.getDB(MongoDB.this.name).getCollection(collection).update(o, object);
                        MongoDB.this.actions.add(storageAction);
                    }
                });
            }
        });
    }
}
