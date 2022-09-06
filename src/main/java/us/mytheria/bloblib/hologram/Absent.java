package us.mytheria.bloblib.hologram;

import org.bukkit.Location;

import java.util.List;

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
}
