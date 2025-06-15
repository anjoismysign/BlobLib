package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.hologram.HologramDriver;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public HologramDriver getHologramDriver() {
        return plugin.getHologramManager().getDriver();
    }

    /**
     * Creates a hologram
     *
     * @param name     name of hologram
     * @param location Bukkit's Location of hologram
     * @param lines    lines of hologram
     * @deprecated Use {@link #getHologramDriver()} instead
     */
    @Deprecated
    public void createHologram(String name, Location location, List<String> lines) {
        getHologramDriver().create(name, location, lines);
    }

    /**
     * Creates a hologram
     *
     * @param name         name of hologram
     * @param location     Bukkit's Location of hologram
     * @param lines        lines of hologram
     * @param saveToConfig if true, hologram will be saved in configuration
     * @deprecated Use {@link #getHologramDriver()} instead
     */
    @Deprecated
    public void createHologram(String name, Location location, List<String> lines, boolean saveToConfig) {
        getHologramDriver().create(name, location, lines, saveToConfig);
    }

    /**
     * Updates a hologram
     *
     * @param name name of hologram
     * @deprecated Use {@link #getHologramDriver()} instead
     */
    @Deprecated
    public void updateHologram(String name) {
        getHologramDriver().update(name);
    }

    /**
     * Deletes a hologram
     *
     * @param name name of hologram
     * @deprecated Use {@link #getHologramDriver()} instead
     */
    @Deprecated
    public void removeHologram(String name) {
        getHologramDriver().remove(name);
    }

    /**
     * Sets a hologram's lines
     *
     * @param name  name of hologram
     * @param lines lines of hologram
     * @deprecated Use {@link #getHologramDriver()} instead
     */
    @Deprecated
    public void setHologramLines(String name, List<String> lines) {
        getHologramDriver().setLines(name, lines);
    }
}
