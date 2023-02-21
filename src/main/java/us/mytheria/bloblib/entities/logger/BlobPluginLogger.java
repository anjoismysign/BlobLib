package us.mytheria.bloblib.entities.logger;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BlobPluginLogger extends ConsoleLogger {
    private final JavaPlugin plugin;

    /**
     * Whenever debugging, will print GOLD messages.
     * Whenever logging, will print GREEN messages.
     * Whenever erroring, will print RED messages.
     * Example output:
     * <p>
     * <{BlobLib}> This is a debug message.
     * </p>
     *
     * @param plugin The plugin to use for the prefix.
     */
    public BlobPluginLogger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(String message) {
        super.log(ChatColor.GREEN + prefix() + start() + message);
    }

    @Override
    public void debug(String message) {
        super.debug(ChatColor.GOLD + prefix() + start() + message);
    }

    @Override
    public void error(String message) {
        super.error(ChatColor.RED + prefix() + start() + message);
    }

    public String prefix() {
        return "<{" + plugin.getName() + "}>";
    }

    public String start() {
        return " ";
    }

    public JavaPlugin javaPlugin() {
        return plugin;
    }
}
