package io.github.anjoismysign.bloblib.entities.logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BlobPluginLogger extends ConsoleLogger {
    private final JavaPlugin plugin;

    /**
     * Whenever debugging, will print GOLD messages.
     * Whenever logging, will print GREEN messages.
     * Whenever an error, will print RED messages.
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

    @Override
    public void throwable(Throwable throwable) {
        error(throwable.getMessage());
    }

    @Override
    public void exception(Exception exception) {
        throwable(exception);
    }

    public String prefix() {
        return "{" + plugin.getName() + "}";
    }

    public String start() {
        return " ";
    }

    public JavaPlugin javaPlugin() {
        return plugin;
    }
}
