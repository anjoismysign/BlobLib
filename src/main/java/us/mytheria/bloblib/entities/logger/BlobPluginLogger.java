package us.mytheria.bloblib.entities.logger;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BlobPluginLogger extends ConsoleLogger {
    private final JavaPlugin plugin;
    private final Logger logger;

    public BlobPluginLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
    }

    @Override
    public void log(String message) {
        logger.info(prefix() + start() + message);
    }

    @Override
    public void debug(String message) {
        logger.info(ChatColor.GREEN + prefix() + start() + message);
    }

    @Override
    public void error(String message) {
        logger.severe(ChatColor.RED + prefix() + start() + message);
    }

    public String prefix() {
        return "<{" + plugin.getName() + "}>";
    }

    public String start() {
        return " ";
    }
}
