package io.github.anjoismysign.bloblib.entities;

import org.bson.types.Binary;
import org.jetbrains.annotations.Nullable;

/**
 * This interface represents an object
 * that holds transient attributes that
 * are later serialized into a {@link BlobCrudable}
 * whenever the object is saved.
 */
public interface BinarySerializable {
    /**
     * Each BlobSerialize should hold a BlobCrudable which
     * will be used to serialize and deserialize
     * all kind of attributes.
     * This is due to allowing the BlobSerializable to be
     * updated with new attributes in the future
     * without having to change anything inside
     * the database.
     *
     * @return the BlobCrudable
     */
    BlobCrudable blobCrudable();

    /**
     * This method should write all the transient
     * attributes to the BlobCrudable that are
     * stored inside the BlobSerializable.
     * <p>
     * Note, it needs to return the updated BlobCrudable
     * so that it can be used in the next method.
     *
     * @return the updated BlobCrudable
     */
    BlobCrudable serializeAllAttributes();

    /**
     * Will provide {@link BlobCrudable#getIdentification()}
     * It may be a UUID or whatever the implementation decides.
     *
     * @return the identification
     */
    default String getIdentification() {
        return blobCrudable().getIdentification();
    }

    /**
     * Will serialize a byte array into the BlobCrudable
     *
     * @param byteArray the byte array
     * @param key       the key
     */
    default void serializeByteArray(byte[] byteArray, String key) {
        blobCrudable().getDocument().put(key, new Binary(byteArray));
    }

    /**
     * Will get a byte array from the BlobCrudable
     *
     * @param key the key
     * @return the byte array
     */
    default byte @Nullable [] getByteArray(String key) {
        try {
            Binary byteArray = (Binary) blobCrudable().getDocument().get(key);
            return byteArray.getData();
        } catch (Exception e) {
            return null;
        }
    }
}