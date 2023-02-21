package us.mytheria.bloblib.managers;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;

/**
 * @author anjoismysign
 * <p>
 * A BlobPlugin is a plugin that makes use of BlobLib's assets features.
 */
public abstract class BlobPlugin extends JavaPlugin {
    private final BlobPluginLogger logger = new BlobPluginLogger(this);

    /**
     * This method is called whenever BlobLib needs to reload all
     * plugins' assets, including this one if correctly registered.
     * <p>
     * NEVER call this method yourself, it SHOULD be called by BlobLib.
     */
    protected void blobLibReload() {
        /*
        In case of being loaded, it will unload
        them backwards since some assets can depend
        on others, such as BlobMessage on BlobSound.
         */
        PluginManager.unloadAssets(this);
        //Loads assets
        PluginManager.loadAssets(this);
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
    public abstract ManagerDirector getManagerDirector();

    @NotNull
    public BlobPluginLogger getAnjoLogger() {
        return logger;
    }
}
