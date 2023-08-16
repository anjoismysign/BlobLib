package us.mytheria.bloblib.entities;

import org.bukkit.plugin.java.JavaPlugin;

public interface PluginUpdater {
    /**
     * Will reload the updater and re-run their checks
     */
    void reload();

    /**
     * Will return true if there is an update available
     *
     * @return true if there is an update available
     */
    boolean hasAvailableUpdate();

    /**
     * Will get last known latest version of the plugin
     *
     * @return the latest version of the plugin
     */
    String getLatestVersion();

    /**
     * Will attempt to download latest version of the plugin
     *
     * @return true if the download was successful
     */
    boolean download();

    /**
     * Will return the plugin that this updater is for
     *
     * @return the plugin that this updater is for
     */
    JavaPlugin getPlugin();
}
