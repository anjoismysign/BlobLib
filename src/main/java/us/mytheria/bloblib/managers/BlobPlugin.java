package us.mytheria.bloblib.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.ConfigDecorator;
import us.mytheria.bloblib.entities.GitHubPluginUpdater;
import us.mytheria.bloblib.entities.PluginUpdater;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;

/**
 * @author anjoismysign
 * <p>
 * A BlobPlugin is a plugin that makes use of BlobLib's assets features.
 */
public abstract class BlobPlugin extends JavaPlugin {
    private final BlobPluginLogger logger = new BlobPluginLogger(this);

    /**
     * Will unregister from BlobLib and
     * call unload method on the ManagerDirector.
     */
    @Override
    public void onDisable() {
        getManagerDirector().unload();
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
    protected void registerToBlobLib(ManagerDirector director) {
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
    public GitHubPluginUpdater generateGitHubUpdater(String repositoryOwner, String repository) {
        return new GitHubPluginUpdater(this, repositoryOwner, repository);
    }

    /**
     * Will get a ConfigDecorator for this BlobPlugin
     *
     * @return The ConfigDecorator
     */
    public ConfigDecorator getConfigDecorator() {
        return new ConfigDecorator(this);
    }
}
