package us.mytheria.bloblib.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that does nothing since the driver is absent.
 */
public class Absent implements HologramDriver {

    @Override
    public void create(String name, Location location, List<String> lines) {
    }

    @Override
    public void create(String name, Location location, List<String> lines, boolean saveToConfig) {
    }

    @Override
    public void update(String name) {
    }

    @Override
    public void remove(String name) {
    }

    @Override
    public void setLines(String name, List<String> lines) {
    }

    @Override
    public @NotNull HologramDriverType getType() {
        return HologramDriverType.ABSENT;
    }
}
