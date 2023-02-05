package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.io.File;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @param <T> The type of object this manager is managing
 * @author An ObjectManager will handle objects that are loaded in
 * random access memory and tracked by a key.
 */
public abstract class ObjectManager<T extends BlobObject> extends Manager {
    private final File loadFilesPath;
    private final Supplier<AbstractMap<String, T>> objectsSupplier;
    private final Supplier<AbstractMap<String, File>> fileSupplier;
    /**
     * The objects that are loaded in random access memory.
     * Should be initialized in loadInConstructor() method.
     */
    private AbstractMap<String, T> objects;
    private AbstractMap<String, File> objectFiles;

    /**
     * Constructor for ObjectManager
     *
     * @param managerDirector The manager director
     * @param loadFilesPath   The path to load files from
     */
    public ObjectManager(ManagerDirector managerDirector, File loadFilesPath,
                         Supplier<AbstractMap<String, T>> supplier,
                         Supplier<AbstractMap<String, File>> fileSupplier) {
        super(managerDirector);
        this.loadFilesPath = loadFilesPath;
        this.objectsSupplier = supplier;
        this.fileSupplier = fileSupplier;
        reload();
    }

    private void initializeObjects() {
        objects = objectsSupplier.get();
        objectFiles = fileSupplier.get();
    }

    public void addObjectFile(String key, File file) {
        objectFiles.put(key, file);
    }

    /**
     * Logic that should run in super() method in constructor.
     */
    @Override
    public void loadInConstructor() {

    }

    /**
     * Will load all files in the given directory
     *
     * @param path The directory to load files from
     */
    public abstract void loadFiles(File path);

    /**
     * Adds an object to the manager and
     * tracks the file it was loaded from.
     *
     * @param key    The key of the object
     * @param file   The file of the object
     * @param object The object
     */
    public void addObject(String key, T object, File file) {
        if (objectFiles.containsKey(key))
            return;
        objects.put(key, object);
        objectFiles.put(key, file);
    }

    /**
     * Adds an object to the manager, saves
     * the object to a file and tracks it.
     *
     * @param key    The key of the object
     * @param object The object
     */
    public void addObject(String key, T object) {
        if (objectFiles.containsKey(key))
            return;
        objects.put(key, object);
        objectFiles.put(key, object.saveToFile());
    }

    /**
     * Removes an object from the manager
     *
     * @param key The key of the object
     */
    public void removeObject(String key) {
        if (objects.containsKey(key))
            return;
        objects.remove(key);
        File file = objectFiles.get(key);
        if (!file.delete())
            getPlugin().getAnjoLogger().singleError("Failed to delete file " + file.getName());
        objectFiles.remove(key);
    }

    /**
     * Will reload the ObjectManager.
     * Initializes the objects AbstractMap,
     * which clears all objects, and then
     * loads all files from the loadFilesPath.
     */
    @Override
    public void reload() {
        initializeObjects();
        loadFiles(loadFilesPath);
    }

    /**
     * @param key The key/fileName of the object
     * @return The object if found, null otherwise
     */
    public T getObject(String key) {
        return objects.get(key);
    }

    /**
     * Will attempt to search for an object that's
     * meant to be stored in this ObjectManager
     * through the provided String 'key'.
     *
     * @param key The key/fileName of the object
     * @return A valid Result object containing the object if found.
     * Invalid otherwise. Be sure to check if the Result is valid
     * through Result#isValid()
     */
    public Result<T> searchObject(String key) {
        return Result.ofNullable(objects.get(key));
    }

    /**
     * Will retrieve all objects as a Collection
     *
     * @return A Collection of all objects
     */
    public Collection<T> values() {
        return objects.values();
    }

    /**
     * Will retrieve the loadFilesPath of the ObjectManager
     *
     * @return The loadFilesPath directory
     */
    public File getLoadFilesDirectory() {
        return loadFilesPath;
    }

    public BlobEditor<String> makeEditor(Player player, String dataType) {
        return BlobEditor.COLLECTION_INJECTION(player.getUniqueId(), dataType, objects.keySet());
    }
}