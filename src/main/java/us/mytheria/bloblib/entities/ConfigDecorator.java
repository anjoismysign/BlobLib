package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ConfigDecorator {
    private final JavaPlugin plugin;

    public ConfigDecorator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Will reload and either create a section or return it if already exists
     *
     * @param path the path to the section
     * @return the section
     */
    public ConfigurationSection reloadAndGetSection(String path) {
        FileConfiguration root = plugin.getConfig();
        root.options().copyDefaults(true);
        ConfigurationSection section;
        if (!root.isConfigurationSection(path))
            section = root.createSection(path);
        else
            section = root.getConfigurationSection(path);
        plugin.saveConfig();
        return section;
    }

    /**
     * Will reload the config and return the listeners section.
     *
     * @return the listeners section
     */
    @NotNull
    public ListenersSection reloadAndGetListeners() {
        return ListenersSection.of(plugin);
    }
}
