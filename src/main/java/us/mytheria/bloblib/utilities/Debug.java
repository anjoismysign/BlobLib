package us.mytheria.bloblib.utilities;

import us.mytheria.bloblib.entities.ConsoleLogger;

public class Debug {
    /**
     * Prints a FINE message to the console.
     *
     * @param message The message to print.
     */
    public static void debug(String message) {
        ConsoleLogger logger = new ConsoleLogger();
        logger.debug(message);
    }

    /**
     * Prints a SEVERE message to the console.
     *
     * @param message
     */
    public static void error(String message) {
        ConsoleLogger logger = new ConsoleLogger();
        logger.error(message);
    }

    /**
     * Prints an INFO message to the console.
     *
     * @param message the message to print
     */
    public static void log(String message) {
        ConsoleLogger logger = new ConsoleLogger();
        logger.log(message);
    }
}
