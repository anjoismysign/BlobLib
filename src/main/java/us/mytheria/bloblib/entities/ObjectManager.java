package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T> The type of object this manager is managing
 * @author An ObjectManager will handle objects that are loaded in
 * random access memory and tracked by a key.
 */
public abstract class ObjectManager<T extends BlobObject> extends Manager {
    private final File loadFilesDirectory;
    private final Supplier<Map<String, T>> objectsSupplier;
    private final Supplier<Map<String, File>> fileSupplier;
    private CompletableFuture<Void> loadFiles;
    /**
     * The objects that are loaded in random access memory.
     * Should be initialized in loadInConstructor() method.
     */
    private Map<String, T> objects;
    private Map<String, File> objectFiles;

    /**
     * Constructor for ObjectManager
     *
     * @param managerDirector    The manager director
     * @param loadFilesDirectory The directory to load files from
     */
    public ObjectManager(ManagerDirector managerDirector, File loadFilesDirectory,
                         Supplier<Map<String, T>> supplier,
                         Supplier<Map<String, File>> fileSupplier) {
        super(managerDirector);
        this.loadFilesDirectory = loadFilesDirectory;
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
    public abstract void loadFiles(File path, CompletableFuture<Void> mainFuture);

    /**
     * Adds an object to the manager and
     * tracks the file it was loaded from.
     * If file is null, the object will not be tracked.
     *
     * @param key    The key of the object
     * @param file   The file of the object
     * @param object The object
     */
    public void addObject(String key, T object, File file) {
        if (objectFiles.containsKey(key))
            return;
        objects.put(key, object);
        if (file != null)
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
        objectFiles.put(key, object.saveToFile(getLoadFilesDirectory()));
    }

    /**
     * Removes an object from the manager
     *
     * @param key The key of the object
     */
    public void removeObject(String key) {
        if (!objects.containsKey(key))
            return;
        objects.remove(key);
        File file = objectFiles.get(key);
        if (file == null)
            return;
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
        updateLoadFiles(getLoadFilesDirectory());
    }

    /**
     * @param key The key/fileName of the object
     * @return The object if found, null otherwise
     */
    @Nullable
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
     * Will retrieve all keys as a Set
     *
     * @return all keys as a Set
     */
    public Set<String> keys() {
        return objects.keySet();
    }

    /**
     * Will retrieve the loadFilesPath of the ObjectManager
     *
     * @return The loadFilesPath directory
     */
    public File getLoadFilesDirectory() {
        return loadFilesDirectory;
    }

    public BlobEditor<String> makeEditor(Player player, String dataType) {
        return BlobEditor.COLLECTION_INJECTION(player.getUniqueId(), dataType, objects.keySet());
    }

    public CompletableFuture<Void> getLoadFiles() {
        return loadFiles;
    }

    public void updateLoadFiles(File path) {
        this.loadFiles = new CompletableFuture<>();
        loadFiles(path, loadFiles);
    }

    public void whenFilesLoad(Consumer<ObjectManager<T>> consumer) {
        BlobPluginLogger logger = getPlugin().getAnjoLogger();
        loadFiles.whenComplete((objectManager, throwable) -> {
            if (throwable != null) {
                logger.singleError(throwable.getMessage());
                return;
            }
            consumer.accept(this);
        });
    }
}