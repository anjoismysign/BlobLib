package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.anjo.entities.Result;
import io.github.anjoismysign.bloblib.entities.logger.BlobPluginLogger;
import io.github.anjoismysign.bloblib.managers.Manager;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;
import io.github.anjoismysign.skeramidcommands.command.CommandTarget;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T> The type of object this manager is managing
 * @author An ObjectManager will handle objects that are loaded in
 * random access memory and tracked by a key.
 */
public abstract class ObjectManager<T extends BlobObject> extends Manager
        implements CommandTarget<T>,
        RunnableReloadable {
    private final File loadFilesDirectory;
    private final Supplier<Map<String, T>> objectsSupplier;
    private final Supplier<Map<String, File>> fileSupplier;
    private final ObjectDirector<T> parent;
    private final List<Runnable> runnableReloadables;
    private CompletableFuture<Void> loadFiles;
    /**
     * The objects that are loaded in random access memory.
     * Should be initialized in loadInConstructor() method.
     */
    private Map<String, T> objects;
    private Map<String, File> objectFiles;

    private boolean isReloading;

    /**
     * Constructor for ObjectManager
     *
     * @param managerDirector    The manager director
     * @param loadFilesDirectory The directory to load files from
     */
    public ObjectManager(ManagerDirector managerDirector, File loadFilesDirectory,
                         Supplier<Map<String, T>> supplier,
                         Supplier<Map<String, File>> fileSupplier,
                         ObjectDirector<T> parent) {
        super(managerDirector);
        this.runnableReloadables = new ArrayList<>();
        this.loadFilesDirectory = loadFilesDirectory;
        this.objectsSupplier = supplier;
        this.fileSupplier = fileSupplier;
        this.parent = parent;
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
     * Will load all files in the given directory
     *
     * @param path The directory to load files from
     */
    public abstract void loadFiles(File path, CompletableFuture<Void> mainFuture);

    /**
     * Loads a file into the ObjectManager.
     * If fails, prints the error to the console.
     *
     * @param file The file to load
     */
    public abstract void loadFile(@NotNull File file, Consumer<Throwable> ifFail);

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

    @Override
    public boolean isReloading() {
        return isReloading;
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
     * Will pick a random object from the manager
     *
     * @return The object wrapped in an Optional.
     * If no objects are in memory, Optional.empty() will be returned.
     */
    public Optional<T> pickRandom() {
        return values()
                .stream()
                .skip(ThreadLocalRandom.current()
                        .nextInt(values().size()))
                .findAny();
    }

    /**
     * Will retrieve the loadFilesPath of the ObjectManager
     *
     * @return The loadFilesPath directory
     */
    public File getLoadFilesDirectory() {
        return loadFilesDirectory;
    }

    /**
     * Will make a BlobEditor for the given player
     *
     * @param player The player to make the editor for
     * @return A BlobEditor for the given player
     */
    public BlobEditor<T> makeEditor(Player player) {
        return BlobEditor.COLLECTION_INJECTION_BUILDER(player.getUniqueId(), values(),
                parent, null);
    }

    public CompletableFuture<Void> getLoadFiles() {
        return loadFiles;
    }

    public void updateLoadFiles(File path) {
        this.loadFiles = new CompletableFuture<>();
        loadFiles(path, loadFiles);
        this.loadFiles.thenRun(() -> {
            isReloading = false;
            runnableReloadables.forEach(Runnable::run);
        });
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

    public ObjectDirector<T> getParent() {
        return parent;
    }

    public List<String> get() {
        return new ArrayList<>(objects.keySet());
    }

    @Nullable
    public T parse(String key) {
        return getObject(key);
    }

    public void whenReloaded(Runnable runnable) {
        runnableReloadables.add(runnable);
    }
}