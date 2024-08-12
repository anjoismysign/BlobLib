package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.Debug;

import java.util.*;

/**
 * @author anjoismysign
 * <p>
 * This class is used to manage all the plugins that use BlobLib
 */
public class PluginManager {
    private static BlobLib blobLib() {
        return BlobLib.getInstance();
    }

    private final Map<String, BlobPlugin> plugins;
    private final List<String> order;

    private final Map<String, Runnable> postWorld;

    /**
     * This constructor is used to initialize the HashMap that will
     * store all the plugins that use BlobLib.
     */
    public PluginManager() {
        plugins = new HashMap<>();
        order = new ArrayList<>();
        postWorld = new HashMap<>();
        Bukkit.getScheduler().runTask(BlobLib.getInstance(), () -> {
            postWorld.values().forEach(Runnable::run);
        });
    }

    private void put(@NotNull BlobPlugin plugin,
                     @NotNull IManagerDirector managerDirector) {
        Objects.requireNonNull(plugin, "BlobPlugin is null!");
        Objects.requireNonNull(managerDirector, "ManagerDirector is null!");
        String name = plugin.getName();
        if (plugins.containsKey(name))
            throw new IllegalArgumentException("BlobPlugin " + name + " is already registered!");
        Debug.log(ColorManager.getRandomColor() + "<{BlobLib}> --> successfully registered "
                + ColorManager.getRandomColor() + name);
        plugins.put(name, plugin);
        order.add(name);
        postWorld.put(name, managerDirector::postWorld);
    }

    private void remove(BlobPlugin plugin) {
        String name = plugin.getName();
        if (!plugins.containsKey(name))
            throw new IllegalArgumentException("BlobPlugin " + name + " was not registered!");
        Debug.log(ColorManager.getRandomColor() + "<{BlobLib}> --> successfully unregistered "
                + ColorManager.getRandomColor() + name);
        plugins.remove(name);
        order.remove(name);
        postWorld.remove(name);
    }

    /**
     * Will reload all BlobPlugin's assets.
     * Will also reload all managers that have the word 'Director' (cAsE sEnSiTiVe)
     * in their name.
     */
    public void reload() {
        plugins.values().forEach(BlobPlugin::blobLibReload);
    }

    /**
     * Will return a Collection of all the BlobPlugins.
     *
     * @return A Collection of all the BlobPlugins.
     */
    @NotNull
    public Collection<BlobPlugin> values() {
        return Objects.requireNonNull(Collections.unmodifiableCollection(plugins.values()));
    }

    /**
     * Will return the BlobPlugin with the given name.
     *
     * @param name The name of the BlobPlugin.
     * @return The BlobPlugin with the given name.
     * null if there is no BlobPlugin with the given name.
     */
    @Nullable
    public BlobPlugin get(String name) {
        return plugins.get(name);
    }

    /**
     * Gets all BlobPlugins in the order they were registered.
     *
     * @return A list of all BlobPlugins in the order they were registered.
     */
    @NotNull
    public List<BlobPlugin> getPlugins() {
        List<BlobPlugin> list = new ArrayList<>();
        order.forEach(name -> {
            BlobPlugin plugin = plugins.get(name);
            if (plugin != null) list.add(plugin);
        });
        return list;
    }

    /**
     * This method should be called whenever a BlobPlugin is enabled.
     * It inserts it inside a HashMap which will be used to call
     * the 'blobLibReload()' method. Will also load all assets
     * that can be used by BlobLib.
     *
     * @param plugin The plugin that is being enabled.
     */
    protected static void registerPlugin(@NotNull BlobPlugin plugin,
                                         @NotNull IManagerDirector managerDirector) {
        PluginManager manager = BlobLib.getInstance().getPluginManager();
        manager.put(plugin, managerDirector);
        loadAssets(plugin, managerDirector);
    }

    /**
     * This method should be called whenever a BlobPlugin is enabled.
     * It inserts it inside a HashMap which will be used to call
     * the 'blobLibReload()' method. Will also load all assets
     * that can be used by BlobLib.
     *
     * @param plugin The plugin that is being enabled.
     */
    public static void registerPlugin(@NotNull BlobPlugin plugin) {
        registerPlugin(plugin, plugin.getManagerDirector());
    }


    /**
     * This method should be called whenever a BlobPlugin is disabled.
     * It removes it from the previously mentioned HashMap.
     * Will also unload all the assets that were loaded by BlobLib.
     *
     * @param plugin The plugin that is being disabled.
     */
    public static void unregisterPlugin(BlobPlugin plugin) {
        unloadAssets(plugin);
        PluginManager manager = BlobLib.getInstance().getPluginManager();
        manager.remove(plugin);
    }

    /**
     * Unloads all assets that were loaded by BlobLib.
     *
     * @param plugin The plugin that is being disabled.
     */
    public static void unloadAssets(BlobPlugin plugin) {
        TranslatableManager.unloadBlobPlugin(plugin);
        blobLib().getTranslatableItemManager().unload(plugin);
        blobLib().getTagSetManager().unload(plugin);
        blobLib().getTranslatablePositionableManager().unload(plugin);
        blobLib().getTranslatableAreaManager().unload(plugin);
        InventoryManager.unloadBlobPlugin(plugin);
        ActionManager.unloadBlobPlugin(plugin);
        MessageManager.unloadBlobPlugin(plugin);
        SoundManager.unloadBlobPlugin(plugin);
    }

    private static void loadAssets(@NotNull BlobPlugin plugin,
                                   @NotNull IManagerDirector director) {
        Objects.requireNonNull(director,
                plugin.getName() + "'s ManagerDirector is null!");
        TranslatableManager.loadBlobPlugin(plugin, director);
        blobLib().getTagSetManager().reload(plugin, director);
        blobLib().getTranslatableItemManager().reload(plugin, director);
        blobLib().getTranslatablePositionableManager().reload(plugin, director);
        blobLib().getTranslatableAreaManager().reload(plugin, director);
        SoundManager.loadBlobPlugin(plugin, director);
        MessageManager.loadBlobPlugin(plugin, director);
        ActionManager.loadBlobPlugin(plugin, director);
        InventoryManager.loadBlobPlugin(plugin, director);
    }

    /**
     * Loads all assets that can be used by BlobLib.
     *
     * @param plugin The plugin that is being enabled.
     */
    public static void loadAssets(BlobPlugin plugin) {
        loadAssets(plugin, plugin.getManagerDirector());
    }

}
