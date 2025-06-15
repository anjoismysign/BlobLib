package io.github.anjoismysign.bloblib.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HologramManager {
    private HologramDriver driver;

    public HologramManager() {
        if (Bukkit.getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
            driver = new DecentHolograms();
            return;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("FancyHolograms") != null) {
            driver = new FancyHolograms();
            return;
        }
        driver = new Absent();
    }

    @NotNull
    public HologramDriver getDriver() {
        return driver;
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
