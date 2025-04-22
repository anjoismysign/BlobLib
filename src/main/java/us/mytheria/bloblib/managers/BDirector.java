package us.mytheria.bloblib.managers;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.annotation.BListener;
import us.mytheria.bloblib.annotation.BManager;
import us.mytheria.bloblib.entities.BlobListener;

import java.util.HashMap;
import java.util.Map;

public class BDirector<T extends ManagerDirector> {
    private final Map<Class<?>, Manager> managerInstances = new HashMap<>();
    private final Map<Class<?>, BlobListener> listenerInstances = new HashMap<>();
    private final T managerDirector;

    public BDirector(@NotNull T managerDirector) {
        this.managerDirector = managerDirector;
        Bukkit.getScheduler().runTask(BlobLib.getInstance(), this::postWorld);
        load();
    }

    private void load() {
        String packageName = managerDirector.getClass().getPackageName();
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(packageName)
                .scan()) {

            // Get all classes annotated with @BManager
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(BManager.class.getName());

            // Filter out classes that are not assignable to Manager (optional but safe)
            classInfoList = classInfoList.filter(classInfo -> classInfo.extendsSuperclass(Manager.class.getName()));

            // Iterate through the found classes and instantiate them
            classInfoList.forEach(classInfo -> {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    Manager instance = (Manager) clazz.getDeclaredConstructor().newInstance();
                    instance.setManagerDirector(managerDirector);
                    managerInstances.put(clazz, instance);
                } catch ( Exception exception ) {
                    System.err.println("Failed to initialize " + classInfo.getName() + ": " + exception.getMessage());
                }
            });

            classInfoList = scanResult.getClassesWithAnnotation(BListener.class.getName());

            classInfoList = classInfoList.filter(classInfo ->
                    classInfo.implementsInterface(BlobListener.class));

            classInfoList.forEach(classInfo -> {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    BlobListener instance = (BlobListener) clazz.getDeclaredConstructor().newInstance();
                    listenerInstances.put(clazz, instance);
                } catch ( Exception exception ) {
                    System.err.println("Failed to initialize " + classInfo.getName() + ": " + exception.getMessage());
                }
            });
        }
    }

    public void postWorld() {
        managerInstances.values().forEach(Manager::postWorld);
    }

    public void unload() {
        managerInstances.values().forEach(Manager::unload);
    }

    public void reload() {
        managerInstances.values().forEach(Manager::reload);
        listenerInstances.values().forEach(BlobListener::reload);
    }

    public Map<Class<?>, Manager> getManagers() {
        return Map.copyOf(managerInstances);
    }

    public Map<Class<?>, BlobListener> getListeners() {
        return Map.copyOf(listenerInstances);
    }
}