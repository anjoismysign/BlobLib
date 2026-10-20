package io.github.anjoismysign.bloblib.manager;

import io.github.anjoismysign.bloblib.listener.SmartTinyListener;
import io.github.anjoismysign.bloblib.utility.DataSerializationFormat;
import io.github.anjoismysign.bloblib.utility.ListenerScanner;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scans the plugin's {@code listener} package for {@link SmartTinyListener}s and
 * registers each one based on its matching boolean property in the configuration file.
 * <p>
 * Listeners are registered as soon as the manager is instantiated. The decision is made
 * against the manager's own loaded configuration, so listeners don't need to reach the
 * manager until they actually handle an event.
 *
 * @param <T> the configuration class
 */
public abstract class SmartListenerManager<T> extends Manager {
    private final Set<SmartTinyListener> scanned;
    private T listenersConfiguration;

    public SmartListenerManager(ManagerDirector managerDirector) {
        super(managerDirector);
        scanned = new HashSet<>();
        var plugin = getPlugin();
        String packageName = plugin.getClass().getPackageName()+".listener";
        List<Listener> listeners = ListenerScanner.scan(
                plugin,
                packageName,
                ListenerScanner.ListenerType.SMART_TINY_LISTENER
        );
        listeners.forEach(listener -> scanned.add((SmartTinyListener) listener));
        reload();
    }

    public abstract Class<T> getConfigurationClass();

    public String fileName(){
        return "listeners";
    }

    public DataSerializationFormat serializationFormat(){
        return DataSerializationFormat.YAML;
    }

    private String resourcePath(){
        return fileName()+"."+serializationFormat().getDefaultExtension();
    }

    private void loadConfiguration(){
        BlobPlugin plugin = getPlugin();
        String resourcePath = resourcePath();
        File listenersFile = new File(plugin.getDataFolder(), resourcePath);
        if (!listenersFile.exists()) {
            plugin.saveResource(resourcePath, false);
        }
        Constructor constructor = new Constructor(getConfigurationClass(), new LoaderOptions());
        Yaml yaml = new Yaml(constructor);
        try (FileInputStream inputStream = new FileInputStream(listenersFile)) {
            T loaded = yaml.load(inputStream);
            if (loaded == null) {
                throw new IllegalStateException("'" + listenersFile.getPath() + "' is empty");
            }
            listenersConfiguration = loaded;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void registerListeners(){
        scanned.forEach(listener -> {
            HandlerList.unregisterAll(listener);
            if (SmartTinyListener.shouldRegister(listenersConfiguration, listener.getClass())) {
                Bukkit.getPluginManager().registerEvents(listener, getPlugin());
            }
        });
    }

    @Override
    public void reload() {
        loadConfiguration();
        registerListeners();
    }

    @NotNull
    public T getListenersConfiguration(){
        return listenersConfiguration;
    }
}
