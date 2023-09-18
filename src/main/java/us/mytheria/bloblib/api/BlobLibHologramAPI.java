package us.mytheria.bloblib.api;

import org.bukkit.Location;
import us.mytheria.bloblib.BlobLib;

import java.util.List;

public class BlobLibHologramAPI {
    private static BlobLibHologramAPI instance;
    private final BlobLib plugin;

    private BlobLibHologramAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibHologramAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibHologramAPI.instance = new BlobLibHologramAPI(plugin);
        }
        return instance;
    }

    public static BlobLibHologramAPI getInstance() {
        return getInstance(null);
    }

    /**
     * Creates a hologram
     *
     * @param name     name of hologram
     * @param location Bukkit's Location of hologram
     * @param lines    lines of hologram
     */
    public void createHologram(String name, Location location, List<String> lines) {
        plugin.getHologramManager().create(name, location, lines);
    }

    /**
     * Creates a hologram
     *
     * @param name         name of hologram
     * @param location     Bukkit's Location of hologram
     * @param lines        lines of hologram
     * @param saveToConfig if true, hologram will be saved in configuration
     */
    public void createHologram(String name, Location location, List<String> lines, boolean saveToConfig) {
        plugin.getHologramManager().create(name, location, lines, saveToConfig);
    }

    /**
     * Updates a hologram
     *
     * @param name name of hologram
     */
    public void updateHologram(String name) {
        plugin.getHologramManager().update(name);
    }

    /**
     * Deletes a hologram
     *
     * @param name name of hologram
     */
    public void removeHologram(String name) {
        plugin.getHologramManager().remove(name);
    }

    /**
     * Sets a hologram's lines
     *
     * @param name  name of hologram
     * @param lines lines of hologram
     */
    public void setHologramLines(String name, List<String> lines) {
        plugin.getHologramManager().setLines(name, lines);
    }
}
