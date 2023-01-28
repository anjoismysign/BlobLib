package us.mytheria.bloblib.managers;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.Debug;

import java.util.HashMap;

/**
 * @author anjoismysign
 * <p>
 * This class is used to manage all the plugins that use BlobLib
 */
public class PluginManager {
    private final HashMap<String, BlobPlugin> plugins;

    /**
     * This constructor is used to initialize the HashMap that will
     * store all the plugins that use BlobLib.
     */
    public PluginManager() {
        plugins = new HashMap<>();
    }

    private void put(BlobPlugin plugin) {
        String name = plugin.getName();
        if (plugins.containsKey(name))
            throw new IllegalArgumentException("BlobPlugin " + name + " is already registered!");
        Debug.log(ColorManager.getRandomColor() + "<{BlobLib}> --> successfully registered "
                + ColorManager.getRandomColor() + name);
        plugins.put(name, plugin);
    }

    private void remove(BlobPlugin plugin) {
        String name = plugin.getName();
        if (!plugins.containsKey(name))
            throw new IllegalArgumentException("BlobPlugin " + name + " was not registered!");
        Debug.log(ColorManager.getRandomColor() + "<{BlobLib}> --> successfully unregistered "
                + ColorManager.getRandomColor() + name);
        plugins.remove(plugin.getName());
    }

    /**
     * Will reload all BlobPlugin's assets.
     */
    public void reload() {
        for (BlobPlugin plugin : plugins.values()) {
            plugin.blobLibReload();
        }
    }

    /**
     * This method should be called whenever a BlobPlugin is enabled.
     * It inserts it inside a HashMap which will be used to call
     * the 'blobLibReload()' method.
     */
    public static void registerPlugin(BlobPlugin plugin) {
        PluginManager manager = BlobLib.getInstance().getPluginManager();
        manager.put(plugin);
    }

    /**
     * This method should be called whenever a BlobPlugin is disabled.
     * It removes it from the previously mentioned HashMap.
     *
     * @param plugin The plugin that is being disabled.
     */
    public static void unregisterPlugin(BlobPlugin plugin) {
        PluginManager manager = BlobLib.getInstance().getPluginManager();
        manager.remove(plugin);
    }

}
