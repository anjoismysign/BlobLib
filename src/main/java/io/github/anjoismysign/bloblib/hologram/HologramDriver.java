package io.github.anjoismysign.bloblib.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HologramDriver {

    /**
     * Creates a Hologram with the given arguments.
     *
     * @param name     Hologram ID
     * @param location Location of the Hologram
     * @param lines    Lines of the Hologram
     */
    void create(String name, Location location, List<String> lines);

    /**
     * Creates a Hologram with the given arguments.
     *
     * @param name         Hologram ID
     * @param location     Location of the Hologram
     * @param lines        Lines of the Hologram
     * @param saveToConfig If true, the Hologram will be saved to the config file
     */
    void create(String name, Location location, List<String> lines, boolean saveToConfig);

    /**
     * Updates a Hologram which ID equals 'name' argument.
     *
     * @param name Hologram ID
     */
    void update(String name);

    /**
     * Removes a Hologram which ID equals 'name' argument.
     *
     * @param name Hologram ID
     */
    void remove(String name);

    /**
     * Sets lines for a Hologram which ID equals 'name' argument.
     *
     * @param name  Hologram ID
     * @param lines Lines to set
     */
    void setLines(String name, List<String> lines);

    /**
     * Gets the driver type
     *
     * @return The driver type
     */
    @NotNull
    HologramDriverType getType();
}
