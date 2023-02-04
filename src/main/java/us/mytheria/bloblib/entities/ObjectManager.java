package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
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
public abstract class ObjectManager<T> extends Manager {
    private final File loadFilesPath;
    private final Supplier<AbstractMap<String, T>> objectsSupplier;
    /**
     * The objects that are loaded in random access memory.
     * Should be initialized in loadInConstructor() method.
     */
    private AbstractMap<String, T> objects;

    /**
     * Constructor for ObjectManager
     *
     * @param managerDirector The manager director
     * @param loadFilesPath   The path to load files from
     */
    public ObjectManager(ManagerDirector managerDirector, File loadFilesPath,
                         Supplier<AbstractMap<String, T>> supplier) {
        super(managerDirector);
        this.loadFilesPath = loadFilesPath;
        this.objectsSupplier = supplier;
        reload();
    }

    private void initializeObjects() {
        objects = objectsSupplier.get();
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
     * Adds an object to the manager
     *
     * @param key    The key of the object
     * @param object The object
     */
    public void addObject(String key, T object) {
        objects.put(key, object);
    }

    /**
     * Removes an object from the manager
     *
     * @param key The key of the object
     */
    public void removeObject(String key) {
        objects.remove(key);
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
}