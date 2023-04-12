package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.entities.inventory.ObjectBuilder;

import java.io.File;

/**
 * Represents an object that can be saved to a YAML file
 * and later be loaded from it.
 * The idea is to allow ObjectManager and ObjectBuilder manage
 * these objects.
 * And example of this is link {@link us.mytheria.bloblib.entities.currency.Currency}.
 * and link {@link us.mytheria.bloblib.entities.currency.CurrencyBuilder}.
 */
public interface BlobObject extends Comparable<BlobObject> {
    /**
     * The key to identify the object by, inside the ObjectManager.
     *
     * @return The key
     */
    String getKey();

    /**
     * By providing File directory (make sure it's File#isDirectory() returns true)
     * the method needs to initialize a File (File#isFile() returns true) inside the directory
     * then load a YamlConfiguration and serialize the BlobObject into it.
     * Remember to save the YamlConfiguration trough FileConfiguration#save(File).
     * Then return the newly created File.
     *
     * @param directory The directory to save the file in
     * @return The file that was saved
     */
    File saveToFile(File directory);

    /**
     * Should return a new ObjectBuilder that will automatically
     * apply the attributes saved inside this BlobObject.
     *
     * @return The ObjectBuilder
     */
    default ObjectBuilder<BlobObject> edit() {
        return null;
    }

    default int compareTo(BlobObject o) {
        return getKey().compareTo(o.getKey());
    }
}
