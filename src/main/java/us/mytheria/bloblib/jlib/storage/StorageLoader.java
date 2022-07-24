package us.mytheria.bloblib.jlib.storage;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/11/15
 */
public abstract class  StorageLoader {
    protected final Storage storage;

    /**
     * Constructs a new StorageLoader instance, shouldn't be used externally
     * @param storage The Storage associated with this StorageLoader
     */
    protected StorageLoader(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns the Storage associated with this StorageLoader
     * @return The Storage
     */
    public final Storage getStorage() {
        return this.storage;
    }

    /**
     * Disconnects from the Storage
     */
    public void disconnect() {
        this.storage.disconnect();
    }
}
