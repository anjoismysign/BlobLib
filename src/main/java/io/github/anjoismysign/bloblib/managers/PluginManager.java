package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.managers.asset.BukkitAssetManager;
import io.github.anjoismysign.bloblib.managers.asset.BukkitGeneratorManager;
import io.github.anjoismysign.bloblib.managers.asset.BukkitIdentityManager;
import io.github.anjoismysign.bloblib.managers.asset.SimpleBukkitAssetManager;
import io.github.anjoismysign.bloblib.managers.asset.SimpleBukkitGeneratorManager;
import io.github.anjoismysign.bloblib.managers.asset.SimpleBukkitIdentityManager;
import io.github.anjoismysign.bloblib.utilities.Debug;
import io.github.anjoismysign.holoworld.asset.AssetGenerator;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.asset.IdentityGenerator;
import io.github.anjoismysign.holoworld.manager.Manager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author anjoismysign
 * <p>
 * This class is used to manage all the plugins that use BlobLib
 */
public class PluginManager {
    private static PluginManager instance;
    private final Map<String, BlobPlugin> plugins;
    private final List<String> order;
    private final Map<String, Runnable> postWorld;
    private final Map<BlobPlugin, List<Class<?>>> pluginManagers = new HashMap<>();
    private final Map<String, Manager> managers = new LinkedHashMap<>();
    private final Map<Class<?>, BukkitAssetManager<?>> assetManagers = new HashMap<>();
    private final Map<Class<?>, BukkitGeneratorManager<?>> generatorManagers = new HashMap<>();
    private final Map<Class<?>, BukkitIdentityManager<?>> identityManagers = new HashMap<>();

    private static BlobLib blobLib() {
        return BlobLib.getInstance();
    }

    @NotNull
    public static PluginManager getInstance() {
        if (instance == null)
            instance = new PluginManager();
        return instance;
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
        PluginManager manager = getInstance();
        plugin.getSerializableManagers().values().forEach(bukkitSerializableManager -> {
            bukkitSerializableManager.syncSaveAll();
            bukkitSerializableManager.unregisterEvents();
        });
        @Nullable List<Class<?>> list = manager.pluginManagers.get(plugin);
        if (list != null)
            list.forEach(clazz -> {
                manager.assetManagers.remove(clazz);
                manager.generatorManagers.remove(clazz);
                String className = clazz.getName();
                manager.managers.remove(className);
            });
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

    /**
     * This constructor is used to initialize the HashMap that will
     * store all the plugins that use BlobLib.
     */
    private PluginManager() {
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
            throw new IllegalArgumentException("BlobPlugin '" + name + "' is already registered!");
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

    public <T extends DataAsset> BukkitAssetManager<T> addAssetManager(
            @NotNull Class<T> clazz,
            @NotNull BlobPlugin plugin,
            boolean failOnNullField) {
        return addAssetManager(clazz, plugin, clazz.getSimpleName(), failOnNullField);
    }

    public <T extends DataAsset> BukkitAssetManager<T> addAssetManager(
            @NotNull Class<T> assetClass,
            @NotNull BlobPlugin plugin,
            @NotNull String name,
            boolean failOnNullField) {
        Objects.requireNonNull(assetClass, "'clazz' cannot be null");
        BukkitAssetManager<T> manager = SimpleBukkitAssetManager.of(
                assetClass,
                plugin,
                name,
                failOnNullField);
        String className = assetClass.getName();
        managers.put(className, manager);
        assetManagers.put(assetClass, manager);
        pluginManagers.computeIfAbsent(plugin, k -> new ArrayList<>()).add(assetClass);
        return manager;
    }

    @Nullable
    public <T extends DataAsset> BukkitAssetManager<T> getAssetManager(Class<?> assetClass) {
        @Nullable BukkitAssetManager<?> assetManager = assetManagers.get(assetClass);
        if (assetManager == null)
            return null;
        return (BukkitAssetManager<T>) assetManager;
    }

    public <T extends DataAsset> BukkitGeneratorManager<T> addGeneratorManager(
            @NotNull Class<? extends AssetGenerator<T>> generatorClass,
            @NotNull BlobPlugin plugin,
            boolean failOnNullField) {
        return addGeneratorManager(generatorClass, plugin, generatorClass.getSimpleName(), failOnNullField);
    }

    public <T extends DataAsset> BukkitGeneratorManager<T> addGeneratorManager(
            @NotNull Class<? extends AssetGenerator<T>> generatorClass,
            @NotNull BlobPlugin plugin,
            @NotNull String name,
            boolean failOnNullField) {
        Objects.requireNonNull(generatorClass, "'generatorClass' cannot be null");
        BukkitGeneratorManager<T> manager = SimpleBukkitGeneratorManager.of(
                generatorClass,
                plugin,
                name,
                failOnNullField);
        String className = generatorClass.getName();
        generatorManagers.put(generatorClass, manager);
        managers.put(className, manager);
        pluginManagers.computeIfAbsent(plugin, k -> new ArrayList<>()).add(generatorClass);
        return manager;
    }

    @Nullable
    public <T extends DataAsset> BukkitGeneratorManager<T> getGeneratorManager(
            Class<? extends AssetGenerator<T>> generatorClass) {
        @Nullable BukkitGeneratorManager<?> generatorManager = generatorManagers.get(generatorClass);
        if (generatorManager == null)
            return null;
        return (BukkitGeneratorManager<T>) generatorManager;
    }

    public <T extends DataAsset> BukkitIdentityManager<T> addIdentityManager(
            @NotNull Class<? extends IdentityGenerator<T>> generatorClass,
            @NotNull BlobPlugin plugin,
            @NotNull String name,
            boolean failOnNullField) {
        Objects.requireNonNull(generatorClass, "'generatorClass' cannot be null");
        BukkitIdentityManager<T> manager = SimpleBukkitIdentityManager.of(
                generatorClass,
                plugin,
                name,
                failOnNullField);
        String className = generatorClass.getName();
        identityManagers.put(generatorClass, manager);
        managers.put(className, manager);
        pluginManagers.computeIfAbsent(plugin, k -> new ArrayList<>()).add(generatorClass);
        return manager;
    }

    @Nullable
    public <T extends DataAsset> BukkitIdentityManager<T> getIdentityManager(
            Class<? extends IdentityGenerator<T>> generatorClass) {
        @Nullable BukkitIdentityManager<?> generatorManager = identityManagers.get(generatorClass);
        if (generatorManager == null)
            return null;
        return (BukkitIdentityManager<T>) generatorManager;
    }

    /**
     * Will reload all BlobPlugin's assets.
     * Will also reload all managers that have the word 'Director' (cAsE sEnSiTiVe)
     * in their name.
     */
    public void reload() {
        plugins.values().forEach(BlobPlugin::blobLibReload);
        managers.values().forEach(Manager::reload);
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

    @NotNull
    public Map<String, BlobPlugin> getPluginsAsMap() {
        return plugins;
    }

}
