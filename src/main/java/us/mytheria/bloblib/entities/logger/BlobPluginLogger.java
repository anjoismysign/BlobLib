package us.mytheria.bloblib.entities.logger;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BlobPluginLogger extends ConsoleLogger {
    private final JavaPlugin plugin;

    public BlobPluginLogger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(String message) {
        super.log(prefix() + start() + message);
    }

    @Override
    public void debug(String message) {
        super.debug(ChatColor.GREEN + prefix() + start() + message);
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
}
