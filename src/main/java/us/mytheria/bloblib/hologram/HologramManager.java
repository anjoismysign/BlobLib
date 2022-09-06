package us.mytheria.bloblib.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class HologramManager {
    private HologramDriver driver;

    public HologramManager() {
        if (Bukkit.getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
            driver = new DecentHolograms();
            return;
        }
        driver = new Absent();
    }

    public void create(String name, Location location, List<String> lines) {
        driver.create(name, location, lines);
    }

    public void create(String name, Location location, List<String> lines, boolean saveToConfig) {
        driver.create(name, location, lines, saveToConfig);
    }

    public void update(String name) {
        driver.update(name);
    }

    public void remove(String name) {
        driver.remove(name);
    }

    public void setLines(String name, List<String> lines) {
        driver.setLines(name, lines);
    }
}
