package us.mytheria.bloblib.hologram;

import org.bukkit.Location;

import java.util.List;

public interface HologramDriver {

    void create(String name, Location location, List<String> lines);

    void create(String name, Location location, List<String> lines, boolean saveToConfig);

    void update(String name);

    void remove(String name);

    void setLines(String name, List<String> lines);
}
