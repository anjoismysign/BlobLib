package us.mytheria.bloblib.entities;

/**
 * This interface represents an object
 * that holds transient attributes that
 * are later serialized into a {@link BlobCrudable}
 * whenever the object is saved.
 */
public interface BlobSerializable {
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
}
