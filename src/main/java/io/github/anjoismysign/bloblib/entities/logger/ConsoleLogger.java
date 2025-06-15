package io.github.anjoismysign.bloblib.entities.logger;

import io.github.anjoismysign.anjo.logger.Logger;
import io.github.anjoismysign.bloblib.managers.ColorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author anjoismysign
 * <p>
 * An object used in io.github.anjoismysign.bloblib.utilities.Debug
 */
public class ConsoleLogger implements Logger {
    private final java.util.logging.Logger logger = Bukkit.getLogger();

    /**
     * Prints a message to the console.
     *
     * @param message the message to print
     */
    @Override
    public void log(String message) {
        logger.info(message);
    }

    /**
     * Prints a fine/debug message to the console.
     *
     * @param message the message to print
     */
    @Override
    public void debug(String message) {
        logger.fine(message);
    }

    /**
     * Prints an error message to the console.
     *
     * @param message the message to print
     */
    @Override
    public void singleError(String message) {
        logger.severe(message);
    }

    /**
     * Prints a warning message to the console.
     *
     * @param throwable the throwable to print
     */
    public void throwable(Throwable throwable) {
        logger.severe(throwable.getMessage());
    }

    /**
     * Prints a warning message to the console.
     *
     * @param exception the exception to print
     */
    public void exception(Exception exception) {
        throwable(exception);
    }

    /**
     * Will get a random color from the ColorManager
     *
     * @return a random color
     */
    public ChatColor getRandomColor() {
        return ColorManager.getRandomColor();
    }
}
