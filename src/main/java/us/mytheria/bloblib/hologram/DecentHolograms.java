package us.mytheria.bloblib.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.List;

public class DecentHolograms implements HologramDriver {

    @Override
    public void create(String name, Location location, List<String> lines) {
        DHAPI.createHologram(name, location, lines);
    }

    @Override
    public void create(String name, Location location, List<String> lines, boolean saveToConfig) {
        DHAPI.createHologram(name, location, saveToConfig, lines);
    }

    @Override
    public void update(String name) {
        DHAPI.updateHologram(name);
    }

    @Override
    public void remove(String name) {
        DHAPI.removeHologram(name);
    }

    @Override
    public void setLines(String name, List<String> lines) {
        Hologram hologram = DHAPI.getHologram(name);
        DHAPI.setHologramLines(hologram, lines);
    }
}
