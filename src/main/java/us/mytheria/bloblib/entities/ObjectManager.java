package us.mytheria.bloblib.entities;

import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public abstract class ObjectManager<T> extends Manager {
    private final File loadFilesPath;
    private HashMap<String, T> objects;

    public ObjectManager(ManagerDirector managerDirector, File loadFilesPath) {
        super(managerDirector);
        this.loadFilesPath = loadFilesPath;
    }

    @Override
    public void loadInConstructor() {
        objects = new HashMap<>();
        loadFiles(loadFilesPath);
    }

    public abstract void loadFiles(File path);

    public void addObject(String key, T object) {
        objects.put(key, object);
    }

    @Override
    public void reload() {
        loadInConstructor();
    }

    /**
     * @param key The key/fileName of the object
     * @return The object if found, null otherwise
     */
    public T getObject(String key) {
        return objects.get(key);
    }

    public Collection<T> values() {
        return objects.values();
    }

    public File getLoadFilesPath() {
        return loadFilesPath;
    }
}