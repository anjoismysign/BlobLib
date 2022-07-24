package us.mytheria.bloblib.jlib.storage.database;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 30/11/15
 */
public interface CallbackHandler<O> {
    /**
     * Calls back an Object
     * @param o The Object
     */
    void callback(O o);
}
