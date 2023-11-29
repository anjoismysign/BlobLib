package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class ConfigDecorator {
    private final JavaPlugin plugin;
    private final ListenersSection listenersSection;

    public ConfigDecorator(JavaPlugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "config.yml");
        ResourceUtil.updateYml(file, plugin);
        plugin.reloadConfig();
        this.listenersSection = ListenersSection.of(plugin);
    }

    private FileConfiguration getRoot() {
        return plugin.getConfig();
    }

    /**
     * Will reload and either create a section or return it if already exists
     *
     * @param path the path to the section
     * @return the section
     */
    public ConfigurationSection reloadAndGetSection(String path) {
        FileConfiguration root = getRoot();
        ConfigurationSection section;
        if (!root.isConfigurationSection(path))
            section = root.createSection(path);
        else
            section = root.getConfigurationSection(path);
        return section;
    }

    /**
     * Will reload the config and return the listeners section.
     *
     * @return the listeners section
     */
    @NotNull
    public ListenersSection reloadAndGetListeners() {
        return listenersSection;
    }
}
