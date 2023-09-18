package us.mytheria.bloblib.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import us.mytheria.bloblib.managers.BlobPlugin;

public class ConfigDecorator {
    private final BlobPlugin plugin;

    public ConfigDecorator(BlobPlugin plugin) {
        this.plugin = plugin;
    }

    public ListenersSection reloadAndGetListeners() {
        FileConfiguration root = plugin.getConfig();
        root.options().copyDefaults(true);
        ConfigurationSection listeners = root.getConfigurationSection("Listeners");
        if (!listeners.isConfigurationSection("TinyListeners"))
            listeners.createSection("TinyListeners");
        if (!listeners.isConfigurationSection("ComplexListeners"))
            listeners.createSection("ComplexListeners");
        if (!listeners.isConfigurationSection("SimpleListeners"))
            listeners.createSection("SimpleListeners");
        ConfigurationSection tinyListeners = listeners.getConfigurationSection("TinyListeners");
        ConfigurationSection complexListeners = listeners.getConfigurationSection("ComplexListeners");
        ConfigurationSection simpleListeners = listeners.getConfigurationSection("SimpleListeners");
        plugin.saveConfig();
        return new ListenersSection(tinyListeners, complexListeners, simpleListeners, root);
    }
}
