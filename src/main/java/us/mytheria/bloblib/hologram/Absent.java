package us.mytheria.bloblib.hologram;

import org.bukkit.Location;

import java.util.List;

/**
 * A class that does nothing since the driver is absent.
 */
public class Absent implements HologramDriver {

    /**
     * Does nothing since the driver is absent
     *
     * @param name     the name of the hologram
     * @param location the location of the hologram
     * @param lines    the lines of the hologram
     */
    @Override
    public void create(String name, Location location, List<String> lines) {
    }

    /**
     * Does nothing since the driver is absent
     *
     * @param name         the name of the hologram
     * @param location     the location of the hologram
     * @param lines        the lines of the hologram
     * @param saveToConfig if the hologram should be saved to the config
     */
    @Override
    public void create(String name, Location location, List<String> lines, boolean saveToConfig) {
    }

    /**
     * Does nothing since the driver is absent
     *
     * @param name the name of the hologram
     */
    @Override
    public void update(String name) {
    }

    /**
     * Does nothing since the driver is absent
     *
     * @param name the name of the hologram
     */
    @Override
    public void remove(String name) {
    }

    /**
     * Does nothing since the driver is absent
     *
     * @param name  the name of the hologram
     * @param lines the lines of the hologram
     */
    @Override
    public void setLines(String name, List<String> lines) {
    }
}
