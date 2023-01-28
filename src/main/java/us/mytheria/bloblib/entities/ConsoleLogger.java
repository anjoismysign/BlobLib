package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.logger.Logger;
import org.bukkit.Bukkit;

/**
 * @author anjoismysign
 * <p>
 * An object used in us.mytheria.bloblib.utilities.Debug
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
     * Prints an error message to the console.
     *
     * @param message the message to print
     */
    @Override
    public void singleError(String message) {
        logger.severe(message);
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
}
