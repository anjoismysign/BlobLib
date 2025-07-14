package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.component.ComponentConsumer;
import io.github.anjoismysign.bloblib.entities.BlobScheduler;
import io.github.anjoismysign.bloblib.entities.ConfigDecorator;
import io.github.anjoismysign.bloblib.entities.GitHubPluginUpdater;
import io.github.anjoismysign.bloblib.entities.PermissionDecorator;
import io.github.anjoismysign.bloblib.entities.PluginUpdater;
import io.github.anjoismysign.bloblib.entities.logger.BlobPluginLogger;
import io.github.anjoismysign.bloblib.managers.serializablemanager.AbstractBukkitSerializableManager;
import io.github.anjoismysign.bloblib.managers.serializablemanager.BukkitSerializableEvent;
import io.github.anjoismysign.bloblib.managers.serializablemanager.BukkitSerializableManager;
import io.github.anjoismysign.bloblib.psa.BukkitDatabaseProvider;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.lehmapp.LehmappCrudable;
import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author anjoismysign
 * <p>
 * A BlobPlugin is a plugin that makes use of BlobLib's assets features.
 */
public abstract class BlobPlugin extends JavaPlugin implements PermissionDecorator, ComponentConsumer {
    private final BlobPluginLogger logger = new BlobPluginLogger(this);
    private final Map<Class<? extends LehmappSerializable>, BukkitSerializableManager<? extends LehmappSerializable>> serializableManagers = new HashMap<>();
    private final BlobScheduler scheduler = new BlobScheduler(this);
    private Permission permission;

    /**
     * Will unregister from BlobLib and
     * call unload method on the ManagerDirector.
     */
    @Override
    public void onDisable() {
        if (getManagerDirector() instanceof ManagerDirector managerDirector) {
            managerDirector.realUnload();
        } else {
            getManagerDirector().unload();
        }
        unregisterFromBlobLib();
    }

    /**
     * This method is called whenever BlobLib needs to reload all
     * plugins' assets, including this one if correctly registered.
     * <p>
     * NEVER call this method yourself, it SHOULD be called by BlobLib.
     */
    protected void blobLibReload() {
        IManagerDirector director = getManagerDirector();
        if (director.isReloading()) {
            Bukkit.getLogger().severe("BlobLib tried to reload " + getName() + " while it was reloading!");
            return;
        }
        /*
        In case of being loaded, it will unload
        them backwards since some assets can depend
        on others, such as BlobMessage on BlobSound.
         */
        PluginManager.unloadAssets(this);
        //Loads assets
        PluginManager.loadAssets(this);
        director.reloadAll();
    }

    /**
     * Call this to register BlobPlugin to BlobLib.
     * It needs to be called after getManagerDirector()
     * is initialized and before getManagerDirector()
     * ObjectDirector's are initialized.
     * <p>
     * Currently, it is called inside ManagerDirector
     * initialization.
     */
    protected void registerToBlobLib(@NotNull ManagerDirector director) {
        PluginManager.registerPlugin(this, director);
    }

    /**
     * You NEED to call this method in your JavaPlugin's onDisable() method,
     * otherwise EXPECT UNWANTED BEHAVIOUR!
     * <p>
     * Note: Be sure to call it after you are sure you don't need BlobLib
     * to reload your plugin files such as BlobMessage's, BlobSound's and BlobInventorie's.
     */
    public void unregisterFromBlobLib() {
        PluginManager.unregisterPlugin(this);
    }

    /**
     * A BlobPlugin is meant to have a ManagerDirector.
     * The idea behind it is, so you can implement your own
     * ManagerDirector and use it to manage your managers.
     *
     * @return The ManagerDirector of the plugin.
     */
    @NotNull
    public abstract IManagerDirector getManagerDirector();

    /**
     * Gets the PluginUpdater of this BlobPlugin.
     * If null, it means the plugin does not have an updater.
     *
     * @return The PluginUpdater of this BlobPlugin.
     */
    @Nullable
    public PluginUpdater getPluginUpdater() {
        return null;
    }

    @NotNull
    public BlobPluginLogger getAnjoLogger() {
        return logger;
    }

    /**
     * Will generate an updater for this BlobPlugin
     *
     * @param repositoryOwner The owner of GitHub repository
     * @param repository      The repository name
     * @return The updater
     */
    @NotNull
    public GitHubPluginUpdater generateGitHubUpdater(String repositoryOwner, String repository) {
        return new GitHubPluginUpdater(this, repositoryOwner, repository);
    }

    /**
     * Will get a ConfigDecorator for this BlobPlugin
     *
     * @return The ConfigDecorator
     */
    @NotNull
    public ConfigDecorator getConfigDecorator() {
        return new ConfigDecorator(this);
    }

    @NotNull
    public BlobScheduler getScheduler() {
        return scheduler;
    }

    @NotNull
    public Permission getPermission() {
        if (permission == null)
            permission = new Permission(getName());
        return permission;
    }

    @NotNull
    public Map<Class<? extends LehmappSerializable>, BukkitSerializableManager<? extends LehmappSerializable>> getSerializableManagers() {
        return serializableManagers;
    }

    @NotNull
    public <T extends LehmappSerializable, S extends BukkitSerializableEvent<T>> BukkitSerializableManager<T> registerSerializableManager(
            @NotNull Class<T> serializableClass,
            @NotNull Function<LehmappCrudable, T> deserializer,
            @Nullable Function<T, S> joinEvent,
            @Nullable Function<T, S> quitEvent,
            @Nullable Supplier<Boolean> eventsRegistrationSupplier) {
        Objects.requireNonNull(deserializer, "'deserializeFunction' cannot be null");
        CrudDatabaseCredentials crudDatabaseCredentials = BukkitDatabaseProvider.INSTANCE.getDatabaseProvider().of(this);
        PermissionDecorator proxy = proxyPermissionDecorator();
        BukkitSerializableManager<T> serializableManager = new AbstractBukkitSerializableManager<>(
                crudDatabaseCredentials,
                deserializer,
                joinEvent,
                quitEvent,
                this,
                eventsRegistrationSupplier) {
            @Override
            public @NotNull BlobScheduler getScheduler() {
                return scheduler;
            }

            @Override
            public @NotNull PermissionDecorator getPermissionDecorator() {
                return proxy;
            }
        };
        serializableManagers.put(serializableClass, serializableManager);
        return serializableManager;
    }
}
