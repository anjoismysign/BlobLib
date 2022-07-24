package us.mytheria.bloblib.jlib.storage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 16/01/16
 */
public class StorageAction {
    private final String timeStamp = new SimpleDateFormat("[dd-MM HH:mm:ss]").format(new Date(System.currentTimeMillis()));
    private final Type type;
    private final String[] data;
    private boolean success;

    /**
     * Constructs a new StorageAction
     * @param type The Type of StorageAction
     * @param data The extra data
     */
    public StorageAction(Type type, String... data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Returns the Type of StorageAction
     * @return The Type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns the extra data
     * @return The data
     */
    public String[] getData() {
        return this.data;
    }

    /**
     * Returns whether this StorageAction was successful
     * @return Whether this StorageAction was successful
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Sets whether this StorageAction was successful
     * @param success Whether this StorageAction was successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return this.timeStamp + " StorageAction: " + this.type + " Data: " + Arrays.toString(this.data) + " Success: " + this.success;
    }

    public enum Type {
        FILE_SAVE,
        FILE_SAVEDEFAULT,
        FILE_GET,
        FILE_RELOAD,
        FILE_GETKEYS,
        JSON_SAVE,
        JSON_SAVEDEFAULT,
        JSON_GET,
        MONGO_GETCONNECTION,
        MONGO_DISCONNECT,
        MONGO_COMMAND,
        MONGO_STORE,
        MONGO_GET,
        MONGO_UPDATE,
        MYSQL_GETCONNECTION,
        REDIS_CONNECT,
        REDIS_DISCONNECT,
        REDIS_SET,
        REDIS_SETMULTIPLE,
        REDIS_GET,
        REDIS_GETMULTIPLE,
        REDIS_EXISTS,
        SQL_EXECUTE,
        SQL_EXECUTEUPDATE,
        SQL_EXECUTEQUERY,
        SQL_HASRESULTSETNEXT,
        SQL_DISCONNECT,
        SQLITE_GETCONNECTION,
    }
}
