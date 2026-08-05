package io.github.anjoismysign.bloblib.utility;

import io.github.anjoismysign.bloblib.logger.ConsoleLogger;

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
