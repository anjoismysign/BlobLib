package us.mytheria.bloblib.managers;

import me.anjoismysign.anjo.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.entities.manager.ManagerDirector;

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
        SoundManager.loadBlobPlugin(this);
        MessageManager.loadBlobPlugin(this);
        InventoryManager.loadBlobPlugin(this);
    }

    /**
     * You NEED to call this method in your JavaPlugin's onEnable() method,
     * otherwise the plugin will not be registered and the blobLibReload() method
     * will NEVER be called, which means that BlobLib will never register your
     * assets.
     */
    public void registerToBlobLib() {
        if (getManagerDirector() == null)
            throw new NullPointerException("ManagerDirector cannot be null! \nBe sure to initialize it before registering to BlobLib!");
        PluginManager.registerPlugin(this);
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
     * The idea behind it is so you can implement your own
     * ManagerDirector and use it to manage your managers.
     *
     * @return The ManagerDirector of the plugin.
     */
    public abstract ManagerDirector getManagerDirector();

    @NotNull
    public Logger getAnjoLogger() {
        return logger;
    }
}
